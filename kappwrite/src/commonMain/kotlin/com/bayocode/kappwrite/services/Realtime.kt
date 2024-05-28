package com.bayocode.kappwrite.services

import com.bayocode.kappwrite.Client
import com.bayocode.kappwrite.Service
import com.bayocode.kappwrite.exceptions.AppwriteException
import com.bayocode.kappwrite.extensions.forEachAsync
import com.bayocode.kappwrite.json
import com.bayocode.kappwrite.models.RealtimeCallback
import com.bayocode.kappwrite.models.RealtimeCode
import com.bayocode.kappwrite.models.RealtimeResponse
import com.bayocode.kappwrite.models.RealtimeResponseEvent
import com.bayocode.kappwrite.models.RealtimeSubscription
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlin.coroutines.CoroutineContext

class Realtime(client: Client) : Service(client), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private companion object {
        private const val TYPE_ERROR = "error"
        private const val TYPE_EVENT = "event"

        private const val DEBOUNCE_MILLIS = 1L

        private var socket: DefaultClientWebSocketSession? = null
        private var activeChannels = mutableSetOf<String>()
        private var activeSubscriptions = mutableMapOf<Int, RealtimeCallback>()

        private var subCallDepth = 0
        private var reconnectAttempts = 0
        private var subscriptionsCounter = 0
        private var reconnect = true
    }

    private fun createSocket() {
        if (activeChannels.isEmpty()) {
            return
        }

        val queryParamBuilder = StringBuilder()
            .append("project=${client.config["project"]}")

        activeChannels.forEach {
            queryParamBuilder
                .append("&channels[]=$it")
        }

        launch {
            if (socket != null) {
                reconnect = false
                closeSocket()
            }
            try {
                socket = client.client.webSocketSession {
                    url("${client.endpointRealtime}/realtime?$queryParamBuilder")
                }
                this@Realtime.launch { loadIncoming() }
            } catch (e: Exception) {
                throw AppwriteException(e.message)
            }
        }
    }

    private suspend fun closeSocket() = kotlin.runCatching {
        socket?.close(CloseReason(code = RealtimeCode.POLICY_VIOLATION.value.toShort(), ""))
        socket?.incoming?.cancel()
    }

    private fun getTimeout() = when {
        reconnectAttempts < 5 -> 1000L
        reconnectAttempts < 15 -> 5000L
        reconnectAttempts < 100 -> 10000L
        else -> 60000L
    }

    fun <T> subscribe(
        vararg channels: String,
        callback: (RealtimeResponseEvent<T>) -> Unit,
    ): RealtimeSubscription {
        val counter = subscriptionsCounter++

        activeChannels.addAll(channels)
        activeSubscriptions[counter] = RealtimeCallback(
            channels.toList(),
            callback as (RealtimeResponseEvent<*>) -> Unit
        )

        launch {
            subCallDepth++
            delay(DEBOUNCE_MILLIS)
            if (subCallDepth == 1) {
                createSocket()
            }
            subCallDepth--
        }

        return RealtimeSubscription {
            activeSubscriptions.remove(counter)
            cleanUp(*channels)
            createSocket()
        }
    }

    private fun cleanUp(vararg channels: String) {
        activeChannels.removeAll { channel ->
            if (!channels.contains(channel)) {
                return@removeAll false
            }
            activeSubscriptions.values.none { callback ->
                callback.channels.contains(channel)
            }
        }
    }

    private suspend fun loadIncoming() = withContext(Dispatchers.IO) {
        reconnectAttempts = 0

        try {
            socket ?: return@withContext
            for (frame in socket!!.incoming) {
                val message = json.decodeFromString<RealtimeResponse>(frame.data.decodeToString())
                when (message.type) {
                    TYPE_ERROR -> handleResponseError(message)
                    TYPE_EVENT -> handleResponseEvent(message)
                }
            }
        } catch (ex: CancellationException) {
            ex.printStackTrace()
            println("XX: Job was cancelled")
            reconnect = false
            return@withContext
        } catch (ex: ClosedReceiveChannelException) {
            ex.printStackTrace()
            val closeReason = socket!!.closeReason.await()
            if (!reconnect || closeReason?.code == RealtimeCode.POLICY_VIOLATION.value.toShort()) {
                reconnect = true
                return@withContext
            }

            val timeout = getTimeout()
            println("Realtime disconnected. Re-connecting in ${timeout / 1000} seconds.")
            createSocket()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleResponseError(message: RealtimeResponse) {
        throw json.decodeFromString<AppwriteException>(json.encodeToString(message.data))
    }

    private suspend fun handleResponseEvent(message: RealtimeResponse) {
        val event = json.decodeFromJsonElement<RealtimeResponseEvent<JsonElement>>(message.data)
        if (event.channels.isEmpty()) {
            return
        }
        if (!event.channels.any { activeChannels.contains(it) }) {
            return
        }
        activeSubscriptions.values.forEachAsync { subscription ->
            if (event.channels.any { subscription.channels.contains(it) }) {
//                event.payload = json.decodeFromString(json.encodeToString(event.payload))
                subscription.callback(event)
            }
        }
    }
}