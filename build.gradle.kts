import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.24"

    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
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
    version.set("1.0.1")
    additionalEditorconfig.putAll(
        mapOf(
            "ktlint_code_style" to "intellij_idea",
            "ktlint_standard_function-signature" to "disabled",
        ),
    )
}

detekt {
    config.from(files("$projectDir/.config/detekt.yml"))
}
