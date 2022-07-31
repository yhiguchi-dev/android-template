plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.dokka")
    id("com.google.firebase.crashlytics")
    id("com.diffplug.spotless")
}

val compose_version: String by project

android {

    compileSdk = 32
    defaultConfig {
        val applicationVersion = System.getProperty("APPLICATION_VERSION")?: "0.1.0"
        applicationId = System.getProperty("APPLICATION_ID")?: "com.example"
        minSdk = 24
        targetSdk = 32
        versionCode = getVersionCode(applicationVersion)
        versionName = applicationVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
        }
        create("release") {
            storeFile = file("release.keystore")
            storePassword = System.getProperty("KEYSTORE_PASSWORD")?: "test"
            keyAlias = System.getProperty("KEY_ALIAS")?: "test"
            keyPassword = System.getProperty("KEY_PASSWORD")?: "test"
        }
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".debug"
            val buildDate = System.getProperty("BUILD_DATE")?: ""
            versionNameSuffix = "-debug-$buildDate"
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = compose_version
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
    lint {
        checkDependencies = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.compose.ui:ui:$compose_version")
    implementation("androidx.compose.material:material:$compose_version")
    implementation("androidx.compose.ui:ui-tooling-preview:$compose_version")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.5.1")
    implementation("androidx.navigation:navigation-compose:2.5.1")

    implementation(platform("com.google.firebase:firebase-bom:30.3.1"))

    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation("com.google.android.gms:play-services-cronet:18.0.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose_version")
    debugImplementation("androidx.compose.ui:ui-tooling:$compose_version")

    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")
    testImplementation("ch.qos.logback:logback-classic:1.2.11")
    testImplementation("io.kotest:kotest-runner-junit5:5.4.1")
    testImplementation("io.kotest:kotest-framework-concurrency:5.4.1")
    testImplementation("io.kotest.extensions:kotest-extensions-wiremock:1.0.3")
    testImplementation("io.mockk:mockk:1.12.5")
    testImplementation("io.mockk:mockk-agent-jvm:1.12.5")

}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint().editorConfigOverride(mapOf("ktlint_code_style" to "android", "indent_size" to 2))
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    dokkaSourceSets {
        named("main") {
            noAndroidSdkLink.set(false)
        }
    }
}

fun getVersionCode(version: String): Int {
    val versions = version.split(".")
    val major = versions[0].toInt() * 1000000
    val minor = versions[1].toInt() * 1000
    val patch = versions[2].toInt()
    return major + minor + patch
}
