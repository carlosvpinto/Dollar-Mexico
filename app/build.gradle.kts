plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

}

android {
    namespace = "com.carlosvpinto.dollar_mexico"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.carlosvpinto.dollar_mexico"
        minSdk = 24
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
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    // Glide para Imágenes
    implementation (libs.circleimageview)
    // RETROFIT2
    implementation(libs.converter.gson)
    implementation(libs.retrofit)
    implementation(libs.okhttp) // La versión puede variar
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation (libs.logging.interceptor)
    implementation(libs.glide)
    implementation(libs.roundedimageview)
    implementation (libs.shimmer)
    implementation (libs.androidx.fragment.ktx)
    implementation (libs.material.v160)
    implementation(libs.androidx.activity) // Revisa la última versión
    implementation (libs.mpandroidchart) //Grafico


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}