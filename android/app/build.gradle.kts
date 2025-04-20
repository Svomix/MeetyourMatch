plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.javanostra.meetyourmatch"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.javanostra.meetyourmatch"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        addManifestPlaceholders(mapOf(
            "VKIDRedirectHost" to "vk.com",
            "VKIDRedirectScheme" to "vk53456162",
            "VKIDClientID" to "53456162",
            "VKIDClientSecret" to "IJHLSPgMRon1txw1hN7p"
        ))
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
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.androidx.core.ktx)
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
    implementation("com.github.MKergall:osmbonuspack:6.9.0") // DON'T TOUCH. Nu ladno, ne budu
    implementation(libs.material)
    implementation("androidx.recyclerview:recyclerview:1.3.2") // DON'T TOUCH. Nu ladno, ne budu
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.wasabeef.glide.transformations)
    implementation(libs.osmdroid.osmdroid.android)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.gson.v2101)
    implementation(libs.vkid)
    implementation(libs.id.onetap.xml)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}