package com.bayocode.kappwrite

class LinuxPlatform: Platform {
    override val name: String = "Linux"
    override val operatingSystem: String = "linux"
    override val deviceInfo: String
        get() = TODO("Not yet implemented")
    override val appVersion: String?
        get() = TODO("Not yet implemented")
    override val version: String?
        get() = TODO("Not yet implemented")
    override val packageName: String?
        get() = TODO("Not yet implemented")
}

actual fun getPlatform(): Platform {
    return LinuxPlatform()
}