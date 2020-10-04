val kotlinProjects: List<String> by extra

dependencies {
    for (lib in kotlinProjects) {
        if (lib != "all") {
            commonMainApi(project(":tools-$lib"))
        }
    }
}
