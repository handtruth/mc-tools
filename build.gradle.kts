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

repositories {
    jcenter()
    repositories {
        maven("https://mvn.handtruth.com")
    }
}

kotlin {
    jvm()
    js {
        nodejs()
        browser {
            testTask {
                useKarma {
                    usePhantomJS()
                }
            }
        }
    }
    sourceSets {
        fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"
        all {
            with(languageSettings) {
                useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
            }
            dependencies {
                val platformVersion: String by project
                val handtruthPlatform = dependencies.platform("com.handtruth.internal:platform:$platformVersion")
                implementation(handtruthPlatform)
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(kotlinx("io"))
                implementation(kotlinx("serialization-runtime-common"))
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlinx("serialization-runtime"))
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(kotlinx("serialization-runtime-js"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
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
