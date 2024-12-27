
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id ("androidx.navigation.safeargs") version "2.5.0" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.20"

}

buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:4.1.1")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.20")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.28.3-alpha")
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.8.4")
    }
}

tasks.register("clean",Delete::class){
    delete(rootProject.buildDir)
}
