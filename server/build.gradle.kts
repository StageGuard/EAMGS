plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}


tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    manifest { attributes("Main-Class" to "me.stageguard.eamuse.ApplicationMainKt") }
}

kotlin {
    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
                optIn("kotlin.ExperimentalStdlibApi")
                optIn("kotlin.contracts.ExperimentalContracts")
            }
        }
    }
}
