plugins {
    kotlin("jvm")
    application
}

application {
    applicationDefaultJvmArgs = listOf("--module-path=/usr/share/openjfx/lib", "--add-modules=javafx.base,javafx.controls")
    mainClassName = "com.handtruth.mc.paket.tool.PaketToolApp"
}

kotlin {
    target {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    sourceSets.all {
        with(languageSettings) {
            useExperimentalAnnotation("kotlinx.coroutines.ExperimentalCoroutinesApi")
            useExperimentalAnnotation("kotlin.RequiresOptIn")
        }
    }
}

configurations.all {
    resolutionStrategy {
        force("org.reflections:reflections:0.9.11")
    }
}

dependencies {

    fun kotlinx(name: String) = "org.jetbrains.kotlinx:kotlinx-$name"
    fun ktor(name: String) = "io.ktor:ktor-$name"

    implementation(project(":tools-paket"))
    implementation(kotlinx("coroutines-javafx"))
    implementation(ktor("client-cio"))
    implementation(ktor("network"))
    implementation(ktor("client-websockets"))
    implementation("no.tornado:tornadofx:1.7.20")
    implementation("org.reflections:reflections")
}
