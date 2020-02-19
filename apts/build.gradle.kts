buildscript {
    repositories {
        jcenter()
        mavenCentral()
    }
}

plugins {
    kotlin("jvm")
    kotlin("kapt")
}

group = "name.utau.anilyrics"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    val googleAutoServiceVersion = "1.0-rc6"
    compileOnly("com.google.auto.service:auto-service:$googleAutoServiceVersion")
    kapt("com.google.auto.service:auto-service:$googleAutoServiceVersion")

    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.3.61")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}