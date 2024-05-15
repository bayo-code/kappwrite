package com.bayocode.kappwrite

import platform.Foundation.NSBundle
import platform.Foundation.NSProcessInfo
import platform.UIKit.UIDevice

class TvOSPlatform: Platform {
    override val name: String = NSProcessInfo.processInfo.operatingSystemName() + " " + NSProcessInfo.processInfo.operatingSystemVersionString()
    override val operatingSystem: String = "ios"
    override val deviceInfo: String = "${UIDevice.currentDevice.model} iOS/${UIDevice.currentDevice.systemVersion}"
    override val version: String = UIDevice.currentDevice.systemVersion
    override val packageName: String? = NSBundle.mainBundle.bundleIdentifier
    override val appVersion: String? = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
}

actual fun getPlatform(): Platform = TvOSPlatform()