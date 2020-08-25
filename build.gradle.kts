@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("com.gladed.androidgitversion")
    kotlin("multiplatform") apply false
}

androidGitVersion {
    prefix = "v"
}

val versionName = androidGitVersion.name()!!
val versionCode = androidGitVersion.code().let { if (it == 0) 1 else it }

allprojects {
    repositories {
        jcenter()
        maven("https://mvn.handtruth.com")
    }
    group = "com.handtruth.mc"
    version = versionName
}

val libModules by extra { listOf("nbt", "paket", "zint", "shared", "chat", "client", "mojang-api") }

fun Project.configureProject() {
    apply<KotlinMultiplatformPluginWrapper>()
    apply<JacocoPlugin>()
    apply<MavenPublishPlugin>()

    configure<KotlinMultiplatformExtension> {
        jvm {
            compilations.all {
                kotlinOptions {
                    jvmTarget = "1.8"
                }
            }
        }

        sourceSets {
            fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"
            fun ktor(name: String) = "io.ktor:ktor-$name"
            val platformVersion: String by project
            val platform = dependencies.platform("com.handtruth.internal:platform:$platformVersion")
            all {
                with(languageSettings) {
                    enableLanguageFeature("InlineClasses")
                    useExperimentalAnnotation("kotlin.RequiresOptIn")
                    useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
                    useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
                    useExperimentalAnnotation("kotlinx.serialization.ImplicitReflectionSerializer")
                    useExperimentalAnnotation("com.handtruth.mc.paket.ExperimentalPaketApi")
                    useExperimentalAnnotation("kotlin.time.ExperimentalTime")
                    useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
                }
                dependencies {
                    api(platform)
                    implementation(platform)
                    compileOnly(platform)
                    runtimeOnly(platform)
                }
            }
            val commonMain by getting {
                dependencies {
                    api(kotlin("stdlib"))
                }
            }
            val commonTest by getting {
                dependencies {
                    implementation(kotlin("test-common"))
                    implementation(kotlin("test-annotations-common"))
                    implementation(ktor("test-dispatcher"))
                }
            }
            val jvmMain by getting {
                dependencies {
                    api(kotlin("stdlib-jdk8"))
                }
            }
            val jvmTest by getting {
                dependencies {
                    implementation(kotlin("test-junit5"))
                    implementation(ktor("test-dispatcher-jvm"))
                    runtimeOnly("org.junit.jupiter:junit-jupiter-engine")
                }
            }
        }
    }

    configure<JacocoPluginExtension> {
        toolVersion = "0.8.5"
        reportsDir = file("${buildDir}/jacoco-reports")
    }


    tasks {
        withType<Test> {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
        }

        val jvmTest by getting {}
        val testCoverageReport by creating(JacocoReport::class) {
            dependsOn(jvmTest)
            group = "Reporting"
            description = "Generate Jacoco coverage reports."
            val coverageSourceDirs = arrayOf(
                    "src/commonMain/kotlin",
                    "src/jvmMain/kotlin"
            )
            val classFiles = file("${buildDir}/classes/kotlin/jvm/")
                    .walkBottomUp()
                    .toSet()
            classDirectories.setFrom(classFiles)
            sourceDirectories.setFrom(files(coverageSourceDirs))
            additionalSourceDirs.setFrom(files(coverageSourceDirs))

            executionData.setFrom(files("${buildDir}/jacoco/jvmTest.exec"))
            reports {
                xml.isEnabled = true
                csv.isEnabled = false
                html.isEnabled = true
                html.destination = file("${buildDir}/jacoco-reports/html")
            }
        }
    }
}

libModules.forEach { project(":tools-$it").configureProject() }
project(":tools-all").configureProject()
