plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id ("kotlin-parcelize")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.kapt")
    id ("androidx.navigation.safeargs")
    id("com.google.dagger.hilt.android")


}

android {
    namespace = "com.android.ecommerce"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.android.ecommerce"
        minSdk = 22
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    //loading button
    implementation("com.github.leandroborgesferreira:loading-button-android:2.3.0")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.13.0")

    //circular image
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //viewpager2 indicator
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    //stepView
    implementation ("com.github.shuhart:stepview:1.5.1")

    //Android Ktx
    implementation ("androidx.navigation:navigation-fragment-ktx:2.4.2")

    //Dagger hilt
    implementation ("com.google.dagger:hilt-android:2.38.1")
    kapt ("com.google.dagger:hilt-compiler:2.38.1")
}

    kapt {
        correctErrorTypes = true
    }
