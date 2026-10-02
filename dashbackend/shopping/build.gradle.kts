plugins {
    id("java") // Forza il sottomodulo a riconoscere l'ambiente Java
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":auth"))

    // Nei test si usa un H2 in memoria: non serve nessun database esterno
    testRuntimeOnly("com.h2database:h2")
}
