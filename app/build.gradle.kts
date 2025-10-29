plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //para el Room
    id("kotlin-kapt")
}

android {
    namespace = "com.aiia.hospital.aiia"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.aiia.hospital.aiia"
        minSdk = 26
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // === TUS DEPENDENCIAS EXISTENTES ===
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.robotemi:sdk:1.136.0")
    implementation("androidx.fragment:fragment-ktx:1.8.2")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // === NUEVAS DEPENDENCIAS AÑADIDAS ===

    // ML Kit (Detección facial)
    implementation("com.google.mlkit:face-detection:16.1.5")

    // CameraX (para análisis en tiempo real del rostro)
    val cameraxVersion = "1.3.4"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:1.3.3")

    // ExoPlayer (para mostrar videos relajantes o motivacionales)
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")

    // Room (para registro local de pacientes / observaciones)
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Retrofit + Gson (para enviar alertas o reportes al tablero médico)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Coroutines (para tareas asíncronas y procesamiento en background)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
