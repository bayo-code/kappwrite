package com.bayocode.kappwrite.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Token
 */
@Serializable
data class Token(
    /**
     * Token ID.
     */
    @SerialName("\$id")
    val id: String,

    /**
     * Token creation date in ISO 8601 format.
     */
    @SerialName("\$createdAt")
    val createdAt: String,

    /**
     * User ID.
     */
    @SerialName("userId")
    val userId: String,

    /**
     * Token secret key. This will return an empty string unless the response is returned using an API key or as part of a webhook payload.
     */
    @SerialName("secret")
    val secret: String,

    /**
     * Token expiration date in ISO 8601 format.
     */
    @SerialName("expire")
    val expire: String,

    /**
     * Security phrase of a token. Empty if security phrase was not requested when creating a token. It includes randomly generated phrase which is also sent in the external resource such as email.
     */
    @SerialName("phrase")
    val phrase: String,

) {
    fun toMap(): Map<String, Any> = mapOf(
        "\$id" to id as Any,
        "\$createdAt" to createdAt as Any,
        "userId" to userId as Any,
        "secret" to secret as Any,
        "expire" to expire as Any,
        "phrase" to phrase as Any,
    )

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun from(
            map: Map<String, Any>,
        ) = Token(
            id = map["\$id"] as String,
            createdAt = map["\$createdAt"] as String,
            userId = map["userId"] as String,
            secret = map["secret"] as String,
            expire = map["expire"] as String,
            phrase = map["phrase"] as String,
        )
    }
}