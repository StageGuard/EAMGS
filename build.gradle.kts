/*
 * Copyright (c) 2022 StageGuard
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20"
    kotlin("plugin.serialization") version "1.6.20"
    id("java")
}

subprojects {

    group = "me.stageguard.eamuse"
    version = "1.0"

    repositories {
        mavenLocal()
        mavenCentral()
    }

    afterEvaluate {
        dependencies {
            implementation("org.jetbrains.kotlinx:atomicfu:0.17.2")
            implementation("org.slf4j:slf4j-api:1.7.30")
            implementation("org.slf4j:slf4j-log4j12:1.7.30")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
            implementation("com.jamesmurty.utils:java-xmlbuilder:1.2")
            implementation("io.netty:netty-all:4.1.63.Final")
            implementation("org.ktorm:ktorm-core:3.4.1")
            implementation("mysql:mysql-connector-java:8.0.25")
            implementation("com.zaxxer:HikariCP:5.0.0")

            testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
            testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
        }

        tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "14" }
        tasks.withType<JavaCompile> { targetCompatibility = "14" }
    }
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "14" }
tasks.withType<JavaCompile> { targetCompatibility = "14" }
