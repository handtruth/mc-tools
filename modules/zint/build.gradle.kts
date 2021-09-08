plugins {
    id("info.solidsoft.pitest") version "1.6.0"
}

kotlin {
    explicitApi()
}

pitest {
    targetClasses.set(listOf("com.handtruth.mc.util.*"))
    junit5PluginVersion.set("0.12")
}

dependencies {
    commonMainApi("io.ktor:ktor-io")
}
