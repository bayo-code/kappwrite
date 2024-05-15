package com.bayocode.kappwrite

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.java.Java
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

internal class AllCertsTrustManager : X509TrustManager {

    @Suppress("TrustAllX509TrustManager")
    override fun checkServerTrusted(
        chain: Array<X509Certificate>,
        authType: String
    ) {
        // no-op
    }

    @Suppress("TrustAllX509TrustManager")
    override fun checkClientTrusted(
        chain: Array<X509Certificate>,
        authType: String
    ) {
        // no-op
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
}

actual fun createHttpClient(selfSigned: Boolean, block: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(Java) {
        engine {
            if (selfSigned) {
                config {
                    val sslSocket = SSLContext.getInstance("SSL").apply {
                        init(null, arrayOf(AllCertsTrustManager()), SecureRandom())
                    }
                    sslContext(sslSocket)
                }
            }
        }
        block()
    }
}