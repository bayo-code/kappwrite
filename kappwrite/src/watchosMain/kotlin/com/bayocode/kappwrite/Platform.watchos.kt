package com.bayocode.kappwrite

import platform.Foundation.NSBundle
import platform.Foundation.NSProcessInfo

class WatchOSPlatform: Platform {
    override val name: String = NSProcessInfo.processInfo.operatingSystemName() + " " + NSProcessInfo.processInfo.operatingSystemVersionString()
    override val operatingSystem: String = "ios"
    override val appVersion: String? = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
    override val deviceInfo: String = "(WatchOS; ${NSProcessInfo.processInfo.operatingSystemVersionString()})"
    override val version: String = NSProcessInfo.processInfo.operatingSystemVersionString()
    override val packageName: String? = NSBundle.mainBundle.bundleIdentifier
}

actual fun getPlatform(): Platform = WatchOSPlatform()