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
    }
    
    val xcf = XCFramework()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosArm64(),
        macosX64(),
        tvosArm64(),
        watchosX64(),
        watchosArm64(),
        tvosX64(),
    ).forEach {
        it.binaries.framework {
            baseName = "kappwrite"
            xcf.add(this)
            isStatic = true
        }
    }

    listOf(
        mingwX64(),
        linuxX64(),
        linuxArm64()
    ).forEach {
        it.binaries.staticLib {
            baseName = "kappwrite"
        }
    }

    jvm()

    val ktor_version = "2.3.11"

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            api("io.ktor:ktor-client-core:2.3.11")
            implementation("io.ktor:ktor-client-logging:$ktor_version")
            implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
            implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
            implementation("com.russhwolf:multiplatform-settings:1.1.1")
            implementation("org.jetbrains.kotlinx:atomicfu:0.24.0")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0-RC.2")
            implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.3.4")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation("io.ktor:ktor-client-okhttp:2.3.11")
            implementation("com.russhwolf:multiplatform-settings-no-arg:1.1.1")
            implementation("androidx.startup:startup-runtime:1.1.1")
        }
        jvmMain.dependencies {
            implementation("io.ktor:ktor-client-java:2.3.11")
        }
        appleMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:2.3.11")
        }
        mingwMain.dependencies {
            implementation("io.ktor:ktor-client-winhttp:2.3.11")
        }
        linuxMain.dependencies {
            implementation("io.ktor:ktor-client-cio:2.3.11")
        }
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
