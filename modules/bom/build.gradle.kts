plugins {
    `java-platform`
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["javaPlatform"])
        }
    }
}

val libModules: List<String> by rootProject.extra

dependencies.constraints {
    val variants = sequenceOf(
        "jvm", "metadata"// "js", "android", "android-debug", //, "wasm32"//, "linuxArm32Hfp", "linuxArm64", "linuxMips32",
        //"linuxMipsel32", "linuxX64", "mingwX86", "mingwX64", "ios", "iosArm32", "iosArm64"
    ).map { it.toLowerCase() }
    fun module(name: String) {
        api("$group:tools-$name:$version")
        variants.forEach {
            api("$group:tools-$name-$it:$version")
        }
    }
    for (lib in libModules) {
        module(lib)
    }
    module("all")
}
