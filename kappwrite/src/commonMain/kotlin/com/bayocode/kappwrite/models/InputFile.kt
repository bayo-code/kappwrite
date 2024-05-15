package com.bayocode.kappwrite.models

class InputFile private constructor() {

    lateinit var path: String
    lateinit var filename: String
    lateinit var mimeType: String
    lateinit var sourceType: String
    lateinit var data: Any

    companion object {
        fun fromBytes(bytes: ByteArray, filename: String = "", mimeType: String = "") = InputFile().apply {
            this.filename = filename
            this.mimeType = mimeType
            data = bytes
            sourceType = "bytes"
        }
    }
}