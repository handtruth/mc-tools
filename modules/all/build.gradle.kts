val libModules: List<String> by rootProject.extra

dependencies {
    for (lib in libModules) {
        commonMainApi(project(":tools-$lib"))
    }
}
