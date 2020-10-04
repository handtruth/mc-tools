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

val kotlinProjects: List<String> by extra

dependencies.constraints {
    fun module(name: String) {
        val p = project(":${rootProject.name}-$name")
        p.extensions.getByType<PublishingExtension>().publications.forEach { it ->
            if (it !is MavenPublication) return@forEach
            api("${it.groupId}:${it.artifactId}:${it.version}")
        }
    }
    for (lib in kotlinProjects) {
        module(lib)
    }
}
