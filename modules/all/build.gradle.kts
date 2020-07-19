val libModules: List<String> by rootProject.extra

dependencies {
    for (lib in libModules) {
        println(lib)
        commonMainApi(project(":tools-$lib"))
    }
}
