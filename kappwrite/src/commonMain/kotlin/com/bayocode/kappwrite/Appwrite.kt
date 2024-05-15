package com.bayocode.kappwrite

object Appwrite {
    private var packageName: String? = null
    private var version: String? = null
    fun initialize(packageName: String, version: String? = null) {
        this.packageName = packageName
        this.version = version
    }

    fun packageName(): String? {
        return getPlatform().packageName ?: packageName
    }

    fun version(): String? {
        return getPlatform().appVersion ?: version
    }
}