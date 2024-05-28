package com.bayocode.kappwrite

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.cValue
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFErrorRefVar
import platform.Foundation.NSOperatingSystemVersion
import platform.Foundation.NSProcessInfo
import platform.Foundation.NSURLAuthenticationMethodServerTrust
import platform.Foundation.NSURLCredential
import platform.Foundation.NSURLSessionAuthChallengeCancelAuthenticationChallenge
import platform.Foundation.NSURLSessionAuthChallengePerformDefaultHandling
import platform.Foundation.NSURLSessionAuthChallengeUseCredential
import platform.Foundation.credentialForTrust
import platform.Foundation.serverTrust
import platform.Security.SecTrustCopyCertificateChain
import platform.Security.SecTrustEvaluate
import platform.Security.SecTrustEvaluateWithError
import platform.Security.SecTrustRef
import platform.Security.SecTrustResultTypeVar
import platform.Security.SecTrustSetAnchorCertificates
import platform.Security.errSecSuccess
import platform.Security.kSecTrustResultInvalid
import platform.Security.kSecTrustResultProceed
import platform.Security.kSecTrustResultUnspecified

@OptIn(ExperimentalForeignApi::class)
actual fun createHttpClient(selfSigned: Boolean, block: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(Darwin) {
        engine {
            if (selfSigned) {
                handleChallenge { session, task, challenge, completionHandler ->
                    // Check that we want to handle this kind of challenge
                    val protectionSpace = challenge.protectionSpace
                    if (protectionSpace.authenticationMethod != NSURLAuthenticationMethodServerTrust) {
                        // Not a 'NSURLAuthenticationMethodServerTrust', default handling...
                        completionHandler(NSURLSessionAuthChallengePerformDefaultHandling.convert(), null)
                        return@handleChallenge
                    }

                    val serverTrust = challenge.protectionSpace.serverTrust
                    if (serverTrust == null) {
                        // Server trust is null, default handling...
                        completionHandler(NSURLSessionAuthChallengePerformDefaultHandling.convert(), null)
                        return@handleChallenge
                    }

                    // Get the servers certs
                    val certChain = SecTrustCopyCertificateChain(serverTrust)
                    // Set those certs as trusted anchors
                    SecTrustSetAnchorCertificates(serverTrust, certChain)

                    if (serverTrust.trustIsValid()) {
                        // ✔ Server trust is valid, continue...
                        val credential = NSURLCredential.credentialForTrust(serverTrust)
                        completionHandler(NSURLSessionAuthChallengeUseCredential.convert(), credential)
                    } else {
                        // ✖ Server trust not valid, cancel challenge...
                        completionHandler(
                            NSURLSessionAuthChallengeCancelAuthenticationChallenge.convert(),
                            null
                        )
                    }
                }
            }
        }
        block()
    }
}

/**
 * Evaluates trust for the specified certificate and policies.
 */
@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
private fun SecTrustRef.trustIsValid(): Boolean {
    var isValid = false

    val version = cValue<NSOperatingSystemVersion> {
        majorVersion = 12
        minorVersion = 0
        patchVersion = 0
    }
    if (NSProcessInfo().isOperatingSystemAtLeastVersion(version)) {
        memScoped {
            val result = alloc<CFErrorRefVar>()
            // https://developer.apple.com/documentation/security/2980705-sectrustevaluatewitherror
            isValid = SecTrustEvaluateWithError(this@trustIsValid, result.ptr)
        }
    } else {
        // https://developer.apple.com/documentation/security/1394363-sectrustevaluate
        memScoped {
            val result = alloc<SecTrustResultTypeVar>()
            result.value = kSecTrustResultInvalid
            val status = SecTrustEvaluate(this@trustIsValid, result.ptr)
            if (status == errSecSuccess) {
                isValid = result.value == kSecTrustResultUnspecified ||
                        result.value == kSecTrustResultProceed
            }
        }
    }

    return isValid
}