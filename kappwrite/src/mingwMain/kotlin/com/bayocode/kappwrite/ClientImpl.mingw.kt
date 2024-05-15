package com.bayocode.kappwrite

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.RegistrySettings
import com.russhwolf.settings.Settings

@OptIn(ExperimentalSettingsImplementation::class)
actual fun createSettings(): Settings {
    return RegistrySettings("com.bayocode.kappwrite.settings")
}