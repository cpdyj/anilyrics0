buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:5.2.0")
    }
}

plugins {
    kotlin("jvm") version "1.3.61"
    kotlin("kapt") version "1.3.61"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    application
}

group = "name.utau.anilyrics"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")

    implementation(vertx("core"))
    implementation(vertx("lang-kotlin"))
    implementation(vertx("lang-kotlin-coroutines"))
    implementation(vertx("web"))
    implementation(vertx("config"))
    implementation(vertx("config-yaml"))
    implementation(vertx("auth-oauth2"))
//    implementation(vertx("auth-jwt"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.+")
    implementation("org.thymeleaf:thymeleaf:3.0.11.RELEASE")
    implementation("org.thymeleaf.extras:thymeleaf-extras-java8time:3.0.4.RELEASE")

//    compileOnly(project(":apts"))
//    kapt(project(":apts"))

    testCompile("org.junit.jupiter:junit-jupiter-api:5.6.0")
}

application {
    mainClassName = "name.utau.anilyrics.MainKt"
}


tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

fun DependencyHandler.vertx(module: String, version: String? = "4.0.0-milestone4"): Any =
    "io.vertx:vertx-$module${version?.let { ":$version" } ?: ""}"

val shadowJar: com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar by tasks
shadowJar.apply {
    dependencies {
//        exclude(project(":apts"))
//        exclude(dependency("org.jetbrains.kotlin:kotlin-compiler-embeddable"))
//        exclude(dependency("org.jetbrains.kotlin:kotlin-script-runtime"))
//        exclude(dependency("org.jetbrains.intellij.deps:trove4j"))

    }
}