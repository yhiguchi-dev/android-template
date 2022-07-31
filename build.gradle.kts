buildscript {
  extra.apply {
    set("compose_version", "1.2.0")
  }
  dependencies {
    classpath("com.google.gms:google-services:4.3.13")
    classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.1")
  }
} // Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  id("com.android.application") version "7.2.1" apply false
  id("com.android.library") version "7.2.1" apply false
  id("org.jetbrains.kotlin.android") version "1.7.10" apply false
  id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10" apply false
  id("org.jetbrains.dokka") version "1.7.10" apply false
  id("com.diffplug.spotless") version "6.9.0" apply true
}

spotless {
  kotlinGradle {
    ktlint().editorConfigOverride(mapOf("indent_size" to 2))
  }
}
