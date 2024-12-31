plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id ("kotlin-parcelize")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.kapt")
    id ("androidx.navigation.safeargs")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.android.ecommerce"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.android.ecommerce"
        minSdk = 23
        //noinspection OldTargetApi
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }


    defaultConfig {
        //applicationId = .....

        //Kapt scehma directory path when created
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }

        // Ensure schema directory creation if doesn't exist
        tasks.register("createSchemaDir") {
            doLast {
                val schemaDir = File("$projectDir/schemas")
                if (!schemaDir.exists()) {
                    schemaDir.mkdirs()
                }
            }
        }

        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            dependsOn("createSchemaDir")
        }
    }

    buildFeatures{
        viewBinding = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.android)
    implementation(libs.androidx.lifecycle.common.jvm)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase dependencies using BOM for compatibility
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-auth")

    // Loading button
    implementation("com.github.leandroborgesferreira:loading-button-android:2.3.0")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.15.0")

    // Circular Image View
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // ViewPager2 indicator
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // StepView
    implementation("com.github.shuhart:stepview:1.5.1")

    // Android Navigation KTX
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.3")

    // Coroutines and Lifecycle
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.6.1")

    // Dagger-Hilt for Dependency Injection
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    // Unit testing
    testImplementation("junit:junit:4.13.2")

    // Android testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}

    kapt {
        correctErrorTypes = true
    }
