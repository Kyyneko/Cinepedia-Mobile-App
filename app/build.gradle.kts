plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.cinepedia"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cinepedia"
        minSdk = 28
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.swiperefreshlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

//    implementation ("io.reactivex.rxjava3:rxjava:3.1.3")
//    implementation ("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    implementation ("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

}