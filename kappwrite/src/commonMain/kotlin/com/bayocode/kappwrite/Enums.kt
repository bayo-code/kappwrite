package com.bayocode.kappwrite

enum class HttpMethod {
    Get, Post, Put, Delete, Patch;

    val nameUppercase: String = name.uppercase()
}

enum class ResponseType {
    Json, Plain, Bytes
}