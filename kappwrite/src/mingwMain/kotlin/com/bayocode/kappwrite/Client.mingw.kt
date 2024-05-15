package com.bayocode.kappwrite

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.winhttp.WinHttp

actual fun createHttpClient(selfSigned: Boolean, block: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(WinHttp) {
        engine {
            sslVerify = !selfSigned
        }
        block()
    }
}