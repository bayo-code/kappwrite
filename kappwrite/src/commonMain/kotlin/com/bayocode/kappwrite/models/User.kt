package com.bayocode.kappwrite.models

import com.bayocode.kappwrite.models.Preferences
import com.bayocode.kappwrite.models.Target
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement


/**
 * User
 */
@Serializable
data class User<T>(
    /**
     * User ID.
     */
    @SerialName("\$id")
    val id: String,

    /**
     * User creation date in ISO 8601 format.
     */
    @SerialName("\$createdAt")
    val createdAt: String,

    /**
     * User update date in ISO 8601 format.
     */
    @SerialName("\$updatedAt")
    val updatedAt: String,

    /**
     * User name.
     */
    @SerialName("name")
    val name: String,

    /**
     * Hashed user password.
     */
    @SerialName("password")
    var password: String? = null,

    /**
     * Password hashing algorithm.
     */
    @SerialName("hash")
    var hash: String? = null,

    /**
     * Password hashing algorithm configuration.
     */
    @SerialName("hashOptions")
    var hashOptions: JsonElement? = null,

    /**
     * User registration date in ISO 8601 format.
     */
    @SerialName("registration")
    val registration: String,

    /**
     * User status. Pass `true` for enabled and `false` for disabled.
     */
    @SerialName("status")
    val status: Boolean,

    /**
     * Labels for the user.
     */
    @SerialName("labels")
    val labels: List<JsonElement>,

    /**
     * Password update time in ISO 8601 format.
     */
    @SerialName("passwordUpdate")
    val passwordUpdate: String,

    /**
     * User email address.
     */
    @SerialName("email")
    val email: String,

    /**
     * User phone number in E.164 format.
     */
    @SerialName("phone")
    val phone: String,

    /**
     * Email verification status.
     */
    @SerialName("emailVerification")
    val emailVerification: Boolean,

    /**
     * Phone verification status.
     */
    @SerialName("phoneVerification")
    val phoneVerification: Boolean,

    /**
     * Multi factor authentication status.
     */
    @SerialName("mfa")
    val mfa: Boolean,

    /**
     * User preferences as a key-value object
     */
    @SerialName("prefs")
    val prefs: Preferences<T>,

    /**
     * A user-owned message receiver. A single user may have multiple e.g. emails, phones, and a browser. Each target is registered with a single provider.
     */
    @SerialName("targets")
    val targets: List<Target>,

    /**
     * Most recent access date in ISO 8601 format. This attribute is only updated again after 24 hours.
     */
    @SerialName("accessedAt")
    val accessedAt: String,

    ) {
    fun toMap(): Map<String, Any> = mapOf(
        "\$id" to id as Any,
        "\$createdAt" to createdAt as Any,
        "\$updatedAt" to updatedAt as Any,
        "name" to name as Any,
        "password" to password as Any,
        "hash" to hash as Any,
        "hashOptions" to hashOptions as Any,
        "registration" to registration as Any,
        "status" to status as Any,
        "labels" to labels as Any,
        "passwordUpdate" to passwordUpdate as Any,
        "email" to email as Any,
        "phone" to phone as Any,
        "emailVerification" to emailVerification as Any,
        "phoneVerification" to phoneVerification as Any,
        "mfa" to mfa as Any,
        "prefs" to prefs.toMap() as Any,
        "targets" to targets.map { it.toMap() } as Any,
        "accessedAt" to accessedAt as Any,
    )
}