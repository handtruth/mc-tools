@file:Suppress("UNUSED_VARIABLE")

plugins {
    id("com.gladed.androidgitversion")
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    `maven-publish`
    jacoco
}

androidGitVersion {
    prefix = "v"
}

group = "com.handtruth.mc"
version = androidGitVersion.name()

val platformVersion: String by project

allprojects {
    repositories {
        jcenter()
        maven("https://mvn.handtruth.com")
        maven("https://dl.bintray.com/korlibs/korlibs/")
    }
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "com.handtruth.internal" && requested.name == "platform")
                useVersion(platformVersion)
        }
    }
}

kotlin {
    jvm()
    val useJS: String by project
    val useJSBool = useJS == "true"
    if (useJSBool)
    js {
        browser {
            testTask {
                useKarma {
                    usePhantomJS()
                }
            }
        }
        nodejs()
    }
    sourceSets {
        fun mc(name: String) = "$group:$name"
        fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"
        all {
            with(languageSettings) {
                enableLanguageFeature("InlineClasses")
                useExperimentalAnnotation("com.handtruth.mc.paket.ExperimentalPaketApi")
                useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
                useExperimentalAnnotation("kotlin.time.ExperimentalTime")
            }
            dependencies {
                val handtruthPlatform = dependencies.platform("com.handtruth.internal:platform:$platformVersion")
                implementation(handtruthPlatform)
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(mc("paket-kotlin:2.3.0"))
                implementation(kotlinx("io"))
                implementation(kotlinx("serialization-runtime-common"))
                implementation("com.soywiz.korlibs.korio:korio")
                implementation("io.ktor:ktor-client-core")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("io.ktor:ktor-test-dispatcher")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(kotlinx("serialization-runtime"))
                implementation("io.ktor:ktor-io-jvm")
                implementation("io.ktor:ktor-client-core-jvm")
                implementation("io.ktor:ktor-client-cio")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("io.ktor:ktor-test-dispatcher-jvm")
            }
        }
        if (useJSBool) {
            val jsMain by getting {
                dependencies {
                    implementation(kotlin("stdlib-js"))
                }
            }
            val jsTest by getting {
                dependencies {
                    implementation(kotlin("test-js"))
                }
            }
        }
    }
}

jacoco {
    toolVersion = "0.8.5"
    reportsDir = file("${buildDir}/jacoco-reports")
}

tasks {
    val jvmTest by getting {}
    val testCoverageReport by creating(JacocoReport::class) {
        dependsOn(jvmTest)
        group = "Reporting"
        description = "Generate Jacoco coverage reports."
        val coverageSourceDirs = arrayOf(
                "commonMain/src",
                "jvmMain/src"
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
