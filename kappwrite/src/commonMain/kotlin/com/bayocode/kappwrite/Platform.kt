package com.bayocode.kappwrite

interface Platform {
    val name: String
    val operatingSystem: String
    val deviceInfo: String
    val appVersion: String?
    val version: String?
    val packageName: String?
}

expect fun getPlatform(): Platform