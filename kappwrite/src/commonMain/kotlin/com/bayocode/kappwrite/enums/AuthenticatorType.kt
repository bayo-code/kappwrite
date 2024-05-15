package com.bayocode.kappwrite.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AuthenticatorType(val value: String) {
    @SerialName("totp")
    TOTP("totp");

    override fun toString() = value
}