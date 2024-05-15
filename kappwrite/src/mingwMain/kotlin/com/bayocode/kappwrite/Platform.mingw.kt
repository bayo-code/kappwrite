package com.bayocode.kappwrite

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.windows.GetComputerNameA
import platform.windows.GetVersionExA
import platform.windows.LPOSVERSIONINFOAVar
import platform.windows.MAX_COMPUTERNAME_LENGTH

class MingWPlatform: Platform {
    override val name: String = "Windows"
    override val operatingSystem: String = "windows"
    override val deviceInfo: String = "(Windows NT; ${getComputerName()})"
    override val appVersion: String? = null
    override val version: String? = getVersion()
    override val packageName: String? = null
}

actual fun getPlatform(): Platform {
    return MingWPlatform()
}

@OptIn(ExperimentalForeignApi::class)
private fun getComputerName(): String = memScoped {
    val buffer = ByteArray(MAX_COMPUTERNAME_LENGTH + 1)
    val size = alloc(MAX_COMPUTERNAME_LENGTH.toUInt() + 1U)

    if (buffer.usePinned { buf ->
        GetComputerNameA(buf.addressOf(0), size.ptr)
    } > 0) {
        buffer.toKString()
    } else {
        "Unknown"
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun getVersion(): String? = memScoped {
    val version: LPOSVERSIONINFOAVar = alloc()
    val result = GetVersionExA(version.value)
    if (result <= 0) { null }
    else "${version.pointed?.dwMajorVersion}.${version.pointed?.dwMinorVersion}.${version.pointed?.dwBuildNumber}"
}