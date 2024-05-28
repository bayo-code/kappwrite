import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinxSerialization)
    id("module.publication")
    `maven-publish`
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        publishAllLibraryVariants()
    }
    
    val xcf = XCFramework()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosArm64(),
        macosX64(),
//        tvosArm64(),
//        watchosX64(),
//        watchosArm64(),
//        tvosX64(),
    ).forEach {
        it.binaries.framework {
            baseName = "kappwrite"
            xcf.add(this)
            isStatic = true
        }
    }

//    listOf(
//        mingwX64(),
//        linuxX64(),
//        linuxArm64()
//    ).forEach {
//        it.binaries.staticLib {
//            baseName = "kappwrite"
//        }
//    }

    jvm("jvm")

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            api(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.multiplatform.settings)
            implementation(libs.atomicfu)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.io.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.multiplatform.settings.no.arg)
            implementation(libs.androidx.startup.runtime)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.java)
        }
        appleMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
//        mingwMain.dependencies {
//            implementation(libs.ktor.client.winhttp)
//        }
//        linuxMain.dependencies {
//            implementation(libs.ktor.client.cio)
//        }
    }
}

android {
    namespace = "com.bayocode.kappwrite"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
        }
    }
}

fun loadEnv() {
    val properties = Properties()
    rootProject.file("local.properties").inputStream().use {
        properties.load(it)
    }

    properties.forEach { (key, value) ->
        ext[key.toString()] = value
    }
}

fun getToken(): String {
    val systemToken: String = System.getenv("GIT_TOKEN") ?: ext["GIT_TOKEN"].toString()
    return systemToken
}

loadEnv()

publishing {
    repositories.maven {
        name = "Gitea"
        url = uri("https://git.braincrunchlabs.tech/api/packages/bayo-code/maven")

        credentials(HttpHeaderCredentials::class) {
            name = "Authorization"
            value = "token ${getToken()}"
        }

        authentication {
            create<HttpHeaderAuthentication>("header")
        }
    }
}

group = "com.bayocode.kappwrite"
version = "0.0.2-SNAPSHOT"
