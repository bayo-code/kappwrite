package com.bayocode.kappwrite.models

import kotlinx.serialization.Serializable

@Serializable
data class UploadProgress(
    val id: String,
    val progress: Double,
    val sizeUploaded: Long,
    val chunksTotal: Int,
    val chunksUploaded: Int
)