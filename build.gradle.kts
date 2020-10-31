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

    implementation("io.arrow-kt:arrow-core:0.11.0")

    testImplementation("org.assertj:assertj-core:3.18.0")
    testImplementation("org.reactivestreams:reactive-streams-tck:1.0.3")
}

tasks.withType<Test> {
    //useJUnitPlatform()
    useTestNG()
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}