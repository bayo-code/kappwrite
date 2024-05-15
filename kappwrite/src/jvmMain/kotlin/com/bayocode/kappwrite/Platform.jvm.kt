package com.bayocode.kappwrite

class JVMPlatform: Platform {
    override val name: String = "JVM ${System.getProperty("os.name")}"
    override val operatingSystem: String = getOperatingSystem()
    override val deviceInfo: String = System.getProperty("os.arch")
    override val version: String? = System.getProperty("os.version")
    override val packageName: String? = null
    override val appVersion: String? = null
}

actual fun getPlatform(): Platform {
    return JVMPlatform()
}

private fun getOperatingSystem(): String {
    val osName = System.getProperty("os.name").lowercase()
    return when {
        osName.contains("win") -> "windows"
        osName.contains("mac") -> "macos"
        osName.contains("linux") -> "linux"
        else -> "Unknown OS"
    }
}

private fun getDeviceInfo(): String {
    val osName = System.getProperty("os.name").lowercase()
    return when {
        osName.contains("win") -> "(Windows NT; ${getComputerName()})"
        osName.contains("mac") -> "(Macintosh; ${System.getProperty("os.version")})"
        osName.contains("linux") -> "(Linux; ${System.getProperty("os.version")})"
        else -> "Unknown OS"
    }
}

private fun getComputerName(): String? = runCatching {
    System.getenv("COMPUTERNAME")
}.getOrElse { "Unknown" }