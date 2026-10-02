plugins {
    java
    id("org.springframework.boot") version "4.1.1" apply false
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

allprojects {
    group = "it.linlo.dashbackend"
    version = "0.1.0"
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    dependencies {
        // Usiamo direttamente le coordinate standard del BOM di Spring Boot 4.1.1
        implementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.1"))

        // Dipendenze ereditate da tutti i sotto-moduli
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-webmvc")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
        testRuntimeOnly("com.h2database:h2")
    }

    // 🚀 AGGIUNGI QUESTO BLOCCO PER CONFIGURARE IL PLUGIN JVM-TEST-SUITE:
    plugins.withType<LifecycleBasePlugin> {
        extensions.findByType<org.gradle.api.plugins.JavaPluginExtension>()?.let { javaExtension ->
            extensions.configure<org.gradle.testing.base.TestingExtension> {
                suites {
                    // Configura la suite di test standard chiamata "test"
                    named<JvmTestSuite>("test") {
                        useJUnitJupiter() // Forza l'uso di JUnit 5 / Jupiter
                        dependencies {
                            // Inietta esplicitamente il launcher dentro il classpath isolato della suite
                            runtimeOnly("org.junit.platform:junit-platform-launcher")
                        }
                    }
                }
            }
        }
    }


    // AGGIUNGI QUESTO BLOCCO QUI SOTTO:
    tasks.test {
        useJUnitPlatform() // Dice a Gradle di usare JUnit Jupiter (JUnit 5)
    }
}
