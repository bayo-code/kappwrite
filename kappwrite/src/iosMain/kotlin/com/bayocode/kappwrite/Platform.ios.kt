package com.bayocode.kappwrite

import platform.Foundation.NSBundle
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val operatingSystem: String = "ios"
    override val deviceInfo: String = "${UIDevice.currentDevice.model} iOS/${UIDevice.currentDevice.systemVersion}"
    override val version: String = UIDevice.currentDevice.systemVersion
    override val packageName: String? = NSBundle.mainBundle.bundleIdentifier
    override val appVersion: String? = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
}

actual fun getPlatform(): Platform = IOSPlatform()