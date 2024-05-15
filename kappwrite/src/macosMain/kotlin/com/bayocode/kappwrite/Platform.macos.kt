package com.bayocode.kappwrite

import platform.Foundation.NSBundle
import platform.Foundation.NSProcessInfo

class MacOSPlatform: Platform {
    override val name: String = NSProcessInfo.processInfo.operatingSystemName() + " " + NSProcessInfo.processInfo.operatingSystemVersionString()
    override val operatingSystem: String = "macos"
    override val deviceInfo: String = "(Macintosh; ${NSProcessInfo.processInfo.operatingSystemVersionString()})"
    override val appVersion: String? = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
    override val version: String = NSProcessInfo.processInfo.operatingSystemVersionString()
    override val packageName: String? = NSBundle.mainBundle.bundleIdentifier
}

actual fun getPlatform(): Platform = MacOSPlatform()