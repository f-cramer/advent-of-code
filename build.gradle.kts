import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"

    id("io.gitlab.arturbosch.detekt") version "1.22.0"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

group = "de.cramer"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.freeCompilerArgs += "-Xcontext-receivers"
}

ktlint {
    version.set("0.45.2")
}

detekt {
    config = files("$projectDir/.config/detekt.yml")
}
