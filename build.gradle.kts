import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.3.0"

    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
}

group = "de.cramer"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.tudo-aqua:z3-turnkey:4.8.14")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    compilerOptions.jvmTarget = JvmTarget.JVM_17
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
