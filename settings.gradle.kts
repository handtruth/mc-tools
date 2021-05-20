pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
    }
    val kotlinVersion: String by settings
    val atomicfuVersion: String by settings
    resolutionStrategy {
        eachPlugin {
            when {
                requested.id.id == "kotlinx-atomicfu" ->
                    useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:$atomicfuVersion")
                requested.id.id.startsWith("org.jetbrains.kotlin") ->
                    useVersion(kotlinVersion)
            }
        }
    }
    val gitVersionPlugin: String by settings
    val dokkaVersion: String by settings
    val ktlintVersion: String by settings
    plugins {
        id("com.gladed.androidgitversion") version gitVersionPlugin
        id("org.jetbrains.dokka") version dokkaVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintVersion
    }
}

rootProject.name = "tools"

val kotlinProjects = listOf(
    "nbt",
    "paket",
    "zint",
    "shared",
    "chat",
    "client",
    "mojang-api",
    "types",
    "internals",
    "collections",
    "graph",
    "all"
)

fun subproject(name: String) {
    include(":${rootProject.name}-$name")
    project(":${rootProject.name}-$name").projectDir = file("modules/$name")
}

subproject("bom")
kotlinProjects.forEach { subproject(it) }

gradle.allprojects {
    extra["kotlinProjects"] = kotlinProjects
}
