package com.bayocode.kappwrite.cookies

import com.bayocode.kappwrite.json
import com.russhwolf.settings.Settings
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.atomicfu.*
import kotlinx.coroutines.sync.withLock
import kotlin.math.min
import kotlinx.datetime.Clock
import io.ktor.util.toLowerCasePreservingASCIIRules
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

class ListenableCookieStorage(
    private val settings: Settings
): CookiesStorage {
    @Serializable
    private data class CookieWithTimestamp(
        @Contextual
        val cookie: Cookie,
        val createdAt: Long
    )

    private val container: MutableList<CookieWithTimestamp> = mutableListOf()
    private val oldestCookie: AtomicLong = atomic(0L)
    private val mutex = Mutex()

    init {
        deserializeCookies()
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        with(cookie) {
            if (name.isBlank()) return
        }

        mutex.withLock {
            container.removeAll { (existingCookie, _) ->
                existingCookie.name == cookie.name && existingCookie.matches(requestUrl)
            }
            val createdAt = Clock.System.now().toEpochMilliseconds()
            container.add(CookieWithTimestamp(cookie.fillDefaults(requestUrl), createdAt))

            cookie.maxAgeOrExpires(createdAt).let {
                if (oldestCookie.value > it) {
                    oldestCookie.value = it
                }
            }
            serializeCookies()
        }
    }

    private fun serializeCookies() {
        val serialized = json.encodeToString(container)
        settings.putString(COOKIES_KEY, serialized)
    }

    private fun deserializeCookies() {
        val serialized = settings.getStringOrNull(COOKIES_KEY) ?: return
        container.clear()
        container.addAll(json.decodeFromString(serialized))
    }

    override suspend fun get(requestUrl: Url): List<Cookie> = mutex.withLock {
        val now = Clock.System.now().toEpochMilliseconds()
        if (now >= oldestCookie.value) cleanup(now)

        val cookies = container.filter { it.cookie.matches(requestUrl) }.map { it.cookie }
        return@withLock cookies
    }

    override fun close() {}

    private fun cleanup(timestamp: Long) {
        container.removeAll { (cookie, createdAt) ->
            val expires = cookie.maxAgeOrExpires(createdAt) ?: return@removeAll false
            expires < timestamp
        }

        val newOldest = container.fold(Long.MAX_VALUE) { acc, (cookie, createdAt) ->
            min(acc, cookie.maxAgeOrExpires(createdAt))
        }

        oldestCookie.value = newOldest
        serializeCookies()
    }

    private fun Cookie.maxAgeOrExpires(createdAt: Long): Long = maxAge.let { createdAt + it * 1000L }

    companion object {
        const val COOKIES_KEY = "__kappwrite_cookies__"
    }
}

internal fun Cookie.matches(requestUrl: Url): Boolean {
    val domain = domain?.toLowerCasePreservingASCIIRules()?.trimStart('.')
        ?: error("Domain field should have the default value")

    val path = with(path) {
        val current = path ?: error("Path field should have the default value")
        if (current.endsWith('/')) current else "$path/"
    }

    val host = requestUrl.host.toLowerCasePreservingASCIIRules()
    val requestPath = let {
        val pathInRequest = requestUrl.encodedPath
        if (pathInRequest.endsWith('/')) pathInRequest else "$pathInRequest/"
    }

    if (host != domain && (hostIsIp(host) || !host.endsWith(".$domain"))) {
        return false
    }

    if (path != "/" &&
        requestPath != path &&
        !requestPath.startsWith(path)
    ) {
        return false
    }

    return !(secure && !requestUrl.protocol.isSecure())
}

internal fun Cookie.fillDefaults(requestUrl: Url): Cookie {
    var result = this

    if (result.path?.startsWith("/") != true) {
        result = result.copy(path = requestUrl.encodedPath)
    }

    if (result.domain.isNullOrBlank()) {
        result = result.copy(domain = requestUrl.host)
    }

    return result
}