plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
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
    implementation ("com.google.firebase:firebase-messaging")
    implementation ("com.google.auth:google-auth-library-oauth2-http:1.19.0")

    implementation("com.github.ZEGOCLOUD:zego_uikit_prebuilt_call_android:+")
    implementation("com.guolindev.permissionx:permissionx:1.8.0")

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.material)




}