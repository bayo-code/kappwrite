package com.bayocode.kappwrite

import android.content.Context
import androidx.startup.Initializer

lateinit var context: Context

class KAppwriteInitializer: Initializer<Unit> {
    override fun create(cntxt: Context) {
        context = cntxt
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

}

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
    override val operatingSystem: String = "android"
    override val deviceInfo: String = "(Linux; U; Android ${android.os.Build.VERSION.RELEASE}; ${android.os.Build.BRAND} ${android.os.Build.MODEL})"
    override val version: String = android.os.Build.VERSION.RELEASE ?: "unknown"
    override val packageName: String by lazy { context.packageName }
    override val appVersion: String? by lazy { context.packageManager.getPackageInfo(context.packageName, 0).versionName }
}

actual fun getPlatform(): Platform = AndroidPlatform()