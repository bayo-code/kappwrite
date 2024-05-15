package com.bayocode.kappwrite

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIO

actual fun createHttpClient(selfSigned: Boolean, block: HttpClientConfig<*>.() -> Unit): HttpClient {
    // TODO: CIO does not support certificate customizations here
    return HttpClient(CIO, block)
}