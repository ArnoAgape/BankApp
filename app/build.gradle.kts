plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.dagger.hilt.android")
  kotlin("kapt")
}

android {
  namespace = "com.aura"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.aura"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
  // define a BOM and its version
  implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
  // define any required OkHttp artifacts without version
  implementation("com.squareup.okhttp3:okhttp")
  implementation("com.squareup.okhttp3:logging-interceptor")
  implementation("androidx.core:core-ktx:1.16.0")
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.annotation:annotation:1.9.1")
  implementation("androidx.constraintlayout:constraintlayout:2.2.1")
  implementation("com.google.dagger:hilt-android:2.50")
  // Moshi JSON Library
  implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
  // Retrofit for Network Requests
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
  implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
  // ViewModel
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
  implementation("androidx.activity:activity-ktx:1.10.1")
  kapt("com.google.dagger:hilt-compiler:2.50")
  // Serializable
  implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
  testImplementation("junit:junit:4.13.2")
  testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
  testImplementation("app.cash.turbine:turbine:1.2.0")
  testImplementation ("org.mockito:mockito-core:5.2.0")
  testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
  testImplementation("androidx.test:core:1.6.1")
  androidTestImplementation("androidx.test.ext:junit:1.2.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
  testImplementation(kotlin("test"))
}