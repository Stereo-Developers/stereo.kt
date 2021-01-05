import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  kotlin("jvm") version "1.4.20"
  java
  application
  id("com.github.johnrengelman.shadow") version "6.1.0"
}

project.version = "3.0.0"
project.group = "xyz.stereobot"
project.setProperty("mainClassName", "xyz.stereobot.bot.Launcher")

val kotlinVersion = KotlinVersion.CURRENT.toString()

repositories {
  jcenter()
  mavenCentral()
  
  maven(url = "https://dl.bintray.com/sedmelluq/com.sedmelluq")
  maven(url = "https://jitpack.io")
}

dependencies {
  // kotlin
  implementation(group = "org.jetbrains.kotlin", name = "kotlin-script-runtime", version = kotlinVersion)
  implementation(group = "org.jetbrains.kotlin", name = "kotlin-compiler-embeddable", version = kotlinVersion)
  implementation(group = "org.jetbrains.kotlin", name = "kotlin-script-util", version = kotlinVersion)
  implementation(group = "org.jetbrains.kotlin", name = "kotlin-scripting-compiler-embeddable", version = kotlinVersion)
  
  // discord shit
  implementation(group = "net.dv8tion", name = "JDA", version = "4.2.0_168")
  implementation(group = "com.github.Devoxin", name = "Flight", version = "2.0.8")
  implementation(group = "com.jagrosh", name = "jda-utilities", version = "3.0.4")
  
  // random ass shit
  implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
  implementation(group = "com.typesafe", name = "config", version = "1.4.0")
  implementation(group = "io.sentry", name = "sentry", version = "3.1.0")
  
  // lavaplayer (+ lavadsp)
  implementation(group = "com.sedmelluq", name = "lavaplayer", version = "1.3.62")
  implementation(group = "com.sedmelluq", name = "lavaplayer-ext-youtube-rotator", version = "0.2.1")
  implementation(group = "com.github.natanbc", name = "lavadsp", version = "0.7.4")
}

application {
  mainClass.set("xyz.stereobot.stereo.Main")
}

tasks.apply {
  withType<KotlinCompile> {
    kotlinOptions.suppressWarnings = true
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
  }
  
  withType<ShadowJar> {
    manifest.attributes.apply {
      put("Main-Class", application.getMainClass())
    }
  }
}
