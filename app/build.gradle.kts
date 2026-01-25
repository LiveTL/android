import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.kotlinx.serialization)
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinter)
}

val appPackageName = "com.livetl.android"

android {
    compileSdk = 36
    namespace = appPackageName

    defaultConfig {
        applicationId = appPackageName
        minSdk = 28
        targetSdk = 36
        versionCode = 305
        versionName = "9.2.1"
    }

    buildFeatures {
        compose = true
        buildConfig = true

        // Disable unused AGP features
        aidl = false
        renderScript = false
        resValues = false
        shaders = false
    }

    buildTypes {
        named("debug") {
            applicationIdSuffix = ".dev"
        }
        named("release") {
            // TODO: for some reason this breaks HoloDex API response parsing
            isShrinkResources = false
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    lint {
        disable.addAll(listOf("MissingTranslation", "ExtraTranslation"))
        enable.addAll(listOf("ObsoleteSdkInt"))

        abortOnError = true
    }

    dependenciesInfo {
        includeInApk = false
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}

val jvmTarget = 21
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(jvmTarget))
    }
}
kotlin {
    jvmToolchain(jvmTarget)
}

dependencies {
    coreLibraryDesugaring(libs.desugar)
    implementation(libs.bundles.coroutines)
    implementation(libs.immutables)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.kotlin.metadata)

    // Logging
    implementation(libs.logcat)

    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.process)

    // Jetpack Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.bundles.compose.debug)
    implementation(libs.bundles.navigation)
    lintChecks(libs.compose.lintchecks)

    // Networking
    implementation(libs.bundles.ktor)
    implementation(libs.serialization)

    // Image loading
    implementation(libs.bundles.coil)

    // Preferences
    implementation(libs.bundles.preferences) {
        exclude("com.github.tfcporciuncula.flow-preferences", "flow-preferences-tests")
    }

    // OSS licenses
    implementation(libs.aboutLibraries.compose)

    // Tests
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.bundles.junit.runtime)
    testImplementation(libs.bundles.mockito)

    // For detecting memory leaks; see https://square.github.io/leakcanary/
    // "debugImplementation"("com.squareup.leakcanary:leakcanary-android:2.2")
}

kotlin {
    compilerOptions {
        // See https://kotlinlang.org/docs/reference/experimental.html#experimental-status-of-experimental-api-markers
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
        )
    }
}

tasks {
    withType<Test> {
        useJUnitPlatform()

        testLogging {
            events(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
            showCauses = true
            showExceptions = true
            showStackTraces = true
        }
    }
}
