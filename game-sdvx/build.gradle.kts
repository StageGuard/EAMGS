plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("java")
}

dependencies {
    api(project(":server"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}


tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
