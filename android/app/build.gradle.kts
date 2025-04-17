plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.javanostra.meetyourmatch"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.javanostra.meetyourmatch"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        buildFeatures {
            viewBinding = true
        }
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    compileOnly(libs.lombok)
    implementation(libs.ucrop)
    implementation(libs.github.glide)
    annotationProcessor(libs.compiler)
    implementation(libs.picasso)
    implementation("com.github.MKergall:osmbonuspack:6.9.0") // DON'T TOUCH
    implementation(libs.material)
    implementation("androidx.recyclerview:recyclerview:1.3.2") // DON'T TOUCH
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.wasabeef.glide.transformations)
    implementation(libs.osmdroid.osmdroid.android)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.gson.v2101)
}