plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
}

group = "fr.notri1.minewolves"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("net.minestom:minestom:2026.03.03-1.21.11")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("org.spongepowered:configurate-yaml:4.2.0")
    implementation("dev.hollowcube:polar:1.15.0")
    implementation("it.unimi.dsi:fastutil:8.5.15")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25)) // Minestom has a minimum Java version of 25
    }
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "fr.notri1.minewolves.MineWolves" // Change this to your main class
        }
    }

    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("") // Prevent the -all suffix on the shadowjar file.
    }
}

tasks.test {
    useJUnitPlatform()
}