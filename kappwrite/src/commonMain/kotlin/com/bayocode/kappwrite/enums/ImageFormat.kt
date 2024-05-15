package com.bayocode.kappwrite.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ImageFormat(val value: String) {
    @SerialName("jpg")
    JPG("jpg"),
    @SerialName("jpeg")
    JPEG("jpeg"),
    @SerialName("gif")
    GIF("gif"),
    @SerialName("png")
    PNG("png"),
    @SerialName("webp")
    WEBP("webp");

    override fun toString() = value
}