import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.4.20"
  java
}

project.version = "3.0.0"
project.group = "gg.stereo"

val kotlinVersion = KotlinVersion.CURRENT.toString()

repositories {
  jcenter()
  mavenCentral()
  
  maven(url = "https://dl.bintray.com/sedmelluq/com.sedmelluq")
  maven(url = "https://jitpack.io")
}

dependencies {
  // kotlin shit
  implementation(group = "org.jetbrains.kotlin", name = "kotlin-script-runtime", version = kotlinVersion)
  implementation(group = "org.jetbrains.kotlin", name = "kotlin-compiler-embeddable", version = kotlinVersion)
  implementation(group = "org.jetbrains.kotlin", name = "kotlin-script-util", version = kotlinVersion)
  implementation(group = "org.jetbrains.kotlin", name = "kotlin-scripting-compiler-embeddable", version = kotlinVersion)
  
  // discord shit
  implementation(group = "net.dv8tion", name = "JDA", version = "4.2.0_168")
  implementation(group = "com.jagrosh", name = "jda-utilities", version = "3.0.4")
  
  // lavaplayer
  implementation(group = "com.sedmelluq", name = "lavaplayer", version = "1.3.62")
  implementation(group = "com.sedmelluq", name = "lavaplayer-ext-youtube-rotator", version = "0.2.1")
  implementation(group = "com.github.natanbc", name = "lavadsp", version = "0.7.4")
  
  // useful shit
  implementation(group = "org.reflections", name = "reflections", version = "0.9.12")
  implementation(group = "org.json", name = "json", version = "20200518")
  implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
  implementation(group = "com.typesafe", name = "config", version = "1.4.0")
}

tasks.apply {
  withType<KotlinCompile> {
    kotlinOptions.suppressWarnings = true
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
  }
}