plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

val sendGridKey = project.rootProject.file("local.properties")
    .readLines()
    .find { it.startsWith("SENDGRID_KEY") }
    ?.split("=")
    ?.getOrNull(1)
    ?.trim()
    ?: ""

android {
    namespace = "com.carlosribeiro.teachtrack"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.carlosribeiro.teachtrack"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // ✅ Segredo vem de local.properties
        buildConfigField("String", "SENDGRID_KEY", "\"$sendGridKey\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // ✅ Necessário para usar BuildConfig
    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.gson)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
