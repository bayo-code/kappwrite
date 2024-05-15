package com.bayocode.kappwrite

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
    val data: T
)