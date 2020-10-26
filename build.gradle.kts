plugins {
    kotlin("jvm") version "1.4.10"
}

group = "de.porsche"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val JUNIT_VERSION = "5.7.0"
dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(platform("io.projectreactor:reactor-bom:Bismuth-RELEASE"))
    implementation("io.projectreactor:reactor-core")
    implementation("io.projectreactor:reactor-test")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$JUNIT_VERSION")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$JUNIT_VERSION")
    testImplementation("org.assertj:assertj-core:3.18.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}