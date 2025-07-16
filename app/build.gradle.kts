plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.app_learn_chinese_2025"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.app_learn_chinese_2025"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(
            "String",
            "CLAUDE_API_KEY",
            "\"${project.findProperty("CLAUDE_API_KEY") ?: "default-key"}\""
        )

        buildConfigField(
            "String",
            "CLAUDE_API_URL",
            "\"https://api.anthropic.com/v1/messages\""
        )
        buildConfigField(
            "String",
            "CLAUDE_API_VERSION",
            "\"2023-06-01\""
        )
        multiDexEnabled = true

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
    buildFeatures {
        buildConfig = true  // Enable BuildConfig generation
    }
    compileOptions {
        encoding = "UTF-8"
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Android core libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.swiperefreshlayout)
    implementation("androidx.multidex:multidex:2.0.1")

    // Room Database
    implementation("androidx.room:room-runtime:2.4.3")
    annotationProcessor("androidx.room:room-compiler:2.4.3")
    implementation("androidx.room:room-rxjava2:2.4.3") // Optional - RxJava2 support for Room
    testImplementation("androidx.room:room-testing:2.4.3") // Optional - Test helpers

    // Camera & Image Processing
    implementation("androidx.camera:camera-camera2:1.2.3")
    implementation("androidx.camera:camera-lifecycle:1.2.3")
    implementation("androidx.camera:camera-view:1.2.3")

    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp for HTTP requests
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // Gson for JSON conversion
    implementation("com.google.code.gson:gson:2.10.1") // Sử dụng phiên bản mới nhất bạn cung cấp

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.15.1") // Sử dụng phiên bản mới nhất bạn cung cấp
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    // Picasso for image loading (giữ lại nếu cần, nếu không thì xóa)
    implementation("com.squareup.picasso:picasso:2.8")

    // ExoPlayer for video playback
    implementation("com.google.android.exoplayer:exoplayer-core:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")

    // MPAndroidChart for charts
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Permissions
    implementation("androidx.activity:activity:1.7.2")
    implementation("androidx.fragment:fragment:1.6.1")

    // Async Operations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Image Utilities
    implementation("androidx.exifinterface:exifinterface:1.3.6")

    // Security (cho API key encryption)
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
//dependencies {
//    implementation(libs.appcompat)
//    implementation(libs.material)
//    implementation(libs.activity)
//    implementation(libs.constraintlayout)
//    implementation(libs.androidx.constraintlayout)
//    implementation(libs.androidx.recyclerview)
//    implementation(libs.androidx.swiperefreshlayout)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.ext.junit)
//    androidTestImplementation(libs.espresso.core)
//
//    // Dependencies from Android
//    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("com.google.android.material:material:1.10.0")
//    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//
//    // Retrofit for API calls
//    implementation("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
//
//    // OkHttp for HTTP requests
//    implementation("com.squareup.okhttp3:okhttp:4.10.0")
//    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
//
//    // Gson for JSON conversion
//    implementation("com.google.code.gson:gson:2.10")
//
//    // Glide for image loading
//    implementation("com.github.bumptech.glide:glide:4.15.1")
//    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
//
//    // Testing dependencies
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//
//    implementation ("androidx.multidex:multidex:2.0.1")
//
//    implementation ("com.google.android.material:material:1.10.0")
//
//    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
//    // For video playback
//    implementation ("com.google.android.exoplayer:exoplayer:2.18.7")
//
//    // For file upload
//    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
//    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")
//
//    // For image loading
//    implementation ("com.github.bumptech.glide:glide:4.14.2")
//    annotationProcessor ("com.github.bumptech.glide:compiler:4.14.2")
//
//    // For permissions
//    implementation ("pub.devrel:easypermissions:3.0.0")
//
//    // Retrofit
//    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
//
//    // OkHttp
//    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
//    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
//
//    // Glide
//    implementation ("com.github.bumptech.glide:glide:4.12.0")
//    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
//
//    implementation ("com.squareup.picasso:picasso:2.8")
//
//    // ExoPlayer cho video
//    implementation ("com.google.android.exoplayer:exoplayer-core:2.19.1")
//    implementation ("com.google.android.exoplayer:exoplayer-ui:2.19.1")
//    implementation ("com.google.android.exoplayer:exoplayer:2.19.1")

//}