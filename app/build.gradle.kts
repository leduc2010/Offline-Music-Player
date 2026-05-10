plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
    id("com.google.firebase.crashlytics")
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.duc.offlinemusicplayer"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.duc.offlinemusicplayer"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    buildFeatures {
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

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.0")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.12.1")


    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Okhttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Lottie
    implementation("com.airbnb.android:lottie:6.6.3")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.57.2")
    ksp("com.google.dagger:hilt-android-compiler:2.57.2")

    //Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Work Hilt
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    implementation("me.relex:circleindicator:2.1.6")

    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-config")

    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.vanniktech:android-image-cropper:4.6.0")
    implementation("jp.wasabeef:blurry:4.0.1")
    implementation("androidx.browser:browser:1.7.0")
    implementation("com.gitee.archermind-ti:autoimageslider:1.0.0-beta")
    implementation("com.asksira.android:loopingviewpager:1.4.1")

    // lib ads
//    implementation("com.github.ngoxuanhungbk:ls-leansoft-publishing-sdk:1.0.35")

    api("org.tensorflow:tensorflow-lite:2.17.0")
    api("com.google.mlkit:face-detection:16.1.7")

    val room_version = "2.7.1"

    implementation("androidx.room:room-runtime:$room_version")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp("androidx.room:room-compiler:$room_version")

    // If this project only uses Java source, use the Java annotationProcessor
    // No additional plugins are necessary
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    implementation("androidx.lifecycle:lifecycle-process:2.8.3")
    implementation("com.google.android.play:app-update-ktx:2.1.0")
    implementation("androidx.paging:paging-runtime:3.2.1")

    implementation("jp.wasabeef:glide-transformations:4.3.0")

    implementation("id.zelory:compressor:3.0.1")

    implementation("com.github.ngoxuanhungbk:ls-leansoft-publishing-sdk:1.0.35")
}