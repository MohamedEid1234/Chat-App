plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    kotlin("plugin.serialization") version "2.0.21"

}

android {
    namespace = "com.example.chat"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.chat"
        minSdk = 26
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes +="META-INF/LICENSE"
            excludes +="META-INF/LICENSE.txt"
            excludes +="META-INF/NOTICE"
            excludes +="META-INF/NOTICE.txt"
            excludes +="mozilla/public-suffix-list.txt"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.volley)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //dagger
    implementation("com.google.dagger:hilt-android:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    ksp("com.google.dagger:hilt-android-compiler:2.52")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // Coil
    implementation("io.coil-kt:coil-compose:2.7.0")
    //firebase dependency
    // Import Firebase BoM for version management
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))

// Firebase Analytics
    implementation("com.google.firebase:firebase-analytics-ktx")

// Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx")

// Firebase Firestore (Database)
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-database-ktx")

// Firebase Cloud Messaging (FCM)
    implementation("com.google.firebase:firebase-messaging-ktx")

// Firebase Crashlytics
    //implementation("com.google.firebase:firebase-crashlytics-ktx")

// Firebase Performance Monitoring
    implementation("com.google.firebase:firebase-perf-ktx")

// Firebase Remote Config
    implementation("com.google.firebase:firebase-config-ktx")

// Firebase Storage (File Storage)
    implementation("com.google.firebase:firebase-storage-ktx")

    //fmc
    implementation ("com.google.firebase:firebase-messaging:23.2.0")
    implementation ("com.google.auth:google-auth-library-oauth2-http:1.19.0")

    //zego
    implementation("com.github.ZEGOCLOUD:zego_uikit_prebuilt_call_android:3.9.1")
    implementation("com.guolindev.permissionx:permissionx:1.8.0")

// Supabase K

    implementation(platform("io.github.jan-tennert.supabase:bom:3.1.0-beta-2"))
    //database
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:realtime-kt")
    //storage
    implementation("io.github.jan-tennert.supabase:storage-kt:1.3.2")
    //auth
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.7.0-beta-1")
    //compose auth (google githup)
    implementation("io.github.jan-tennert.supabase:compose-auth:1.3.2")
    implementation("io.github.jan-tennert.supabase:compose-auth-ui:1.3.2")

    // Coil
    implementation("io.coil-kt:coil-compose:2.5.0")



    // Ktor Client Engine (for Android)
    implementation("io.ktor:ktor-client-android:3.0.3")


    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.material)

   /* implementation ("com.zegocloud.uikit:call:1.3.7") // Example version, check for the latest version in Zego's documentation
    implementation ("com.zegocloud:uikit:zego-uikit-sdk:2.0.0" ) // Check the latest version on the official repo
*/



}