import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application)
    id("com.mikepenz.aboutlibraries.plugin")
    kotlin("android")
    kotlin("plugin.serialization")
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinter)
}

val appPackageName = "com.livetl.android"

android {
    compileSdk = 34
    namespace = appPackageName

    defaultConfig {
        applicationId = appPackageName
        minSdk = 21
        targetSdk = 34
        versionCode = 289
        versionName = "8.0.5"
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

    flavorDimensions.add("variant")
    productFlavors {
        create("playstore") {
            dimension = "variant"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.version.get()
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar)
    implementation(libs.bundles.coroutines)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Logging
    implementation(libs.timber)

    implementation(libs.androidx.core)

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

    // For detecting memory leaks; see https://square.github.io/leakcanary/
    // "debugImplementation"("com.squareup.leakcanary:leakcanary-android:2.2")
}

tasks {
    // Requires the submodules to already be initialized, i.e.:
    // git submodule update --init --recursive
    val buildExtensionSource by register("buildExtensionSource") {
        doLast {
            exec {
                workingDir = File("../extension/LiveTL")
                setCommandLine("yarn", "install")
            }
            exec {
                workingDir = File("../extension/LiveTL")
                setCommandLine("yarn", "build", "android")
            }
        }
    }

    val copyExtensionArtifacts by register<Copy>("copyExtensionArtifacts") {
        dependsOn(buildExtensionSource)
        from("../extension/LiveTL/build")
        into("./src/main/assets")
        include("**/*")
    }

    project.afterEvaluate {
        tasks.findByName("mergePlaystoreReleaseAssets")?.dependsOn(copyExtensionArtifacts)
    }

    withType<KotlinCompile> {
        // See https://kotlinlang.org/docs/reference/experimental.html#experimental-status-of-experimental-api-markers
        kotlinOptions.freeCompilerArgs += listOf(
            "-opt-in=kotlin.Experimental",
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi",
        )
    }
}
