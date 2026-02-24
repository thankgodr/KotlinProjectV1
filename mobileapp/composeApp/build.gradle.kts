import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.google.services)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    val iosTargets = listOf(
        iosArm64(),
        iosSimulatorArm64()
    )

    iosTargets.forEach { target ->
        target.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.androidx.navigation.compose)

                api(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
                implementation(libs.koin.annotations)


                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)

                implementation("com.woowla.compose.icon.collections:fontawesome:7.1.0")

            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.compose.uiToolingPreview)
                implementation(libs.androidx.activity.compose)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.koin.android)

                // Firestore android dependency
                implementation("com.google.firebase:firebase-firestore-ktx:24.10.3")
            }
        }

        // create shared iosMain source set
        val iosMain by creating {
            dependsOn(commonMain)
        }

        // make ios target main source sets depend on iosMain
        iosTargets.forEach { target ->
            val targetMain = getByName("${target.targetName}Main")
            targetMain.dependsOn(iosMain)
            targetMain.dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}

// Koin KSP
dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
    add("kspAndroid", libs.koin.ksp.compiler)
    add("kspIosSimulatorArm64", libs.koin.ksp.compiler)
    add("kspIosArm64", libs.koin.ksp.compiler)
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
    arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
}

kotlin.sourceSets.commonMain {
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}

// Ensure KSP metadata generates code before all other compile/ksp tasks
project.tasks.configureEach {
    if (name != "kspCommonMainKotlinMetadata" &&
        (name.startsWith("ksp") || name.startsWith("compile"))
    ) {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

android {
    namespace = "me.thankgodr.fintechchallegeapp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "me.thankgodr.fintechchallegeapp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}