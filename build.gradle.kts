import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.4.20"
  java
}

project.version = "1.0.0"
project.group = "xyz.stereobot.kt"

repositories {
  jcenter()
  mavenCentral()
  
  maven(url = "https://dl.bintray.com/sedmelluq/com.sedmelluq")
  maven(url = "https://jitpack.io")
}

dependencies {
  // language shit
  testCompile(group = "junit", name = "junit", version = "4.12")
  implementation(group = "org.codehaus.groovy", name = "groovy-jsr223", version = "3.0.0-alpha-4")
  
  // discord shit
  implementation(group = "net.dv8tion", name = "JDA", version = "4.2.0_168")
  implementation(group = "com.jagrosh", name = "jda-utilities", version = "3.0.4")
  
  // mongodb
  implementation(group = "org.mongodb", name = "mongo-java-driver", version = "3.12.7")
  
  // json related shit
  implementation(group = "com.github.agcom.bson", name = "bson-serialization", version = "0.5.0")
  implementation(group = "com.github.agcom.bson", name = "bson-mongodb", version = "0.5.0")
  implementation(group = "org.json", name = "json", version = "20200518")
  
  // random ass shit
  implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
  implementation(group = "com.typesafe", name = "config", version = "1.4.0")
  implementation(group = "com.github.jkcclemens", name = "khttp", version= "0.1.0")
  
  // lavaplayer (+ lavadsp)
  implementation(group = "com.sedmelluq", name = "lavaplayer", version = "1.3.62")
  implementation(group = "com.sedmelluq", name = "lavaplayer-ext-youtube-rotator", version = "0.2.1")
  implementation(group = "com.github.natanbc", name = "lavadsp", version = "0.7.4")
}

tasks.withType<KotlinCompile> {
  kotlinOptions.suppressWarnings = true
  kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}