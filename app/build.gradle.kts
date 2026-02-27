plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    //id("com.google.gms.google-services")
    id("org.jetbrains.kotlinx.kover")
}

android {
    namespace = "it.carmelolagamba.saveyourtime"
    compileSdk = 36

    defaultConfig {
        applicationId = "it.carmelolagamba.saveyourtime"
        minSdk = 28
        targetSdk = 34
        versionCode = 36
        versionName = "1.2.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.work:work-runtime-ktx:2.7.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.7")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.hilt:hilt-common:1.3.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:core:1.5.0")

    // Dependency Injection
    val hiltVersion = "2.51.1"
    implementation("com.google.dagger:hilt-android:${hiltVersion}")
    kapt("com.google.dagger:hilt-compiler:${hiltVersion}")
    androidTestImplementation("com.google.dagger:hilt-android-testing:${hiltVersion}")
    kaptAndroidTest("com.google.dagger:hilt-compiler:${hiltVersion}")
    testImplementation("com.google.dagger:hilt-android-testing:${hiltVersion}")
    kaptTest("com.google.dagger:hilt-compiler:${hiltVersion}")

    // SQL Lite & Room
    val roomVersion = "2.6.1"

    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")

    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$roomVersion")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$roomVersion")

    // optional - RxJava2 support for Room
    implementation("androidx.room:room-rxjava2:$roomVersion")

    // optional - RxJava3 support for Room
    implementation("androidx.room:room-rxjava3:$roomVersion")

    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation("androidx.room:room-guava:$roomVersion")

    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$roomVersion")
    androidTestImplementation("androidx.room:room-testing:$roomVersion")

    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:$roomVersion")

    // Vico
    implementation("com.patrykandpatrick.vico:compose:1.12.0")
    implementation("com.patrykandpatrick.vico:compose-m2:1.12.0")
    implementation("com.patrykandpatrick.vico:compose-m3:1.12.0")
    implementation("com.patrykandpatrick.vico:core:1.12.0")
    implementation("com.patrykandpatrick.vico:views:1.12.0")

    // Ballon
    implementation("com.github.skydoves:balloon:1.6.4")
}

buildscript {
    repositories {
        mavenCentral()
        maven{
            url = uri("https://jitpack.io")
        }
    }
}


kapt {
    correctErrorTypes = true
}