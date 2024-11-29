plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.gachon.twitter"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gachon.twitter"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3" // Compose 버전에 맞는 Kotlin Compiler Extension
    }
}

dependencies {
    // Core libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose libraries
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation") // Foundation 라이브러리
    implementation(platform("androidx.compose:compose-bom:2024.02.00")) // BOM으로 통일
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material") // Material 라이브러리
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core)
    implementation(libs.androidx.core)
    implementation(libs.androidx.core)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.core)
    implementation(files("..\\libs\\mysql-connector-java-5.1.49.jar"))
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Test dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    //mysql
    //implementation fileTree(dir: "C:\Users\yesjae\AndroidStudioProjects\Twitter\.idea\modules\app\lib\", include: ["*.jar"])
    //implementation("mysql:mysql-connector-java:8.0.15")

    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    //implementation("com.mysql:mysql-connector-j:8.0.31")
    // Debug dependencies
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
