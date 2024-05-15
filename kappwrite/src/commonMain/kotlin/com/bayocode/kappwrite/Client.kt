package com.bayocode.kappwrite

import com.bayocode.kappwrite.cookies.ListenableCookieStorage
import com.bayocode.kappwrite.cookies.serializers
import com.bayocode.kappwrite.exceptions.AppwriteException
import com.russhwolf.settings.Settings
import com.bayocode.kappwrite.models.InputFile
import com.bayocode.kappwrite.models.UploadProgress
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.forms.ChannelProvider
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.InputProvider
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.escapeIfNeeded
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.source
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

val json: Json = Json {
    isLenient = true
    ignoreUnknownKeys = true
    prettyPrint = true
    serializersModule = serializers
}

data class FileToUpload(
    val filename: String,
    val contentType: ContentType? = null,
    val channelProvider: ChannelProvider? = null,
    val inputProvider: InputProvider? = null
)

class Client(
    endpoint: String = "https://cloud.appwrite.io/v1",
    selfSigned: Boolean = false
): CoroutineScope {
    val headers = mutableMapOf<String, String>()
    private var initializing = false
    var config: MutableMap<String, String>

    lateinit var endpoint: String
        private set

    var endpointRealtime: String? = null
        private set

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private val job = Job()

    private fun createClient(selfSigned: Boolean) = createHttpClient(selfSigned) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
        install(ContentNegotiation) {
            json(json)
        }
        install(HttpCookies) {
            storage = ListenableCookieStorage(createSettings())
        }
        install(WebSockets)
    }

    var client: HttpClient = createClient(false)

    init {
        setEndpoint(endpoint)
        setSelfSigned(selfSigned)
        config = mutableMapOf()
        setEndpointRealtime(
            endpoint
                .replaceFirst("https://", "wss://")
                .replaceFirst("http://", "ws://")
        )

        if (!endpoint.startsWith("http://") && !endpoint.startsWith("https://")) {
            throw AssertionError("Invalid endpoint. Does not start with http/https")
        }

        initialize()
    }

    private fun initialize() {
        if (initializing) return
        initializing = true

        // TODO: Revisit these!
        headers.clear()
        headers.putAll(
            mapOf(
                "content-type" to "application/json",
                "x-sdk-name" to "Flutter",
                "x-sdk-platform" to "client",
                "x-sdk-language" to "flutter",
                "x-sdk-version" to "0.0.1",
                "X-Appwrite-Response-Format" to "1.5.0",
                "origin" to "appwrite-${getPlatform().operatingSystem}://${Appwrite.packageName()}",
                "user-agent" to "${Appwrite.packageName()}/${Appwrite.version()} ${getPlatform().deviceInfo}"
            )
        )

        initializing = false
    }

    suspend fun webAuth(url: String, callbackUrlScheme: String?) {
        TODO("Not yet implemented")
    }

    fun setSelfSigned(status: Boolean): Client {
        client.close()
        client = createClient(status)
        return this
    }

    fun setEndpoint(endpoint: String): Client {
        this.endpoint = endpoint
        if (this.endpointRealtime == null) {
            this.endpointRealtime = endpoint
                .replaceFirst("https://", "wss://")
                .replaceFirst("http://", "ws://")
        }
        return this
    }

    fun setEndpointRealtime(endpoint: String): Client {
        this.endpointRealtime = endpoint
        return this
    }

    fun setProject(project: String): Client {
        addHeader("X-Appwrite-Project", project)
        config["project"] = project
        return this
    }

    fun setJWT(value: String): Client {
        config["JWT"] = value
        addHeader("X-Appwrite-JWT", value)
        return this
    }

    fun setLocale(value: String): Client {
        config["locale"] = value
        addHeader("X-Appwrite-Locale", value)
        return this
    }

    fun setSession(value: String): Client {
        config["session"] = value
        addHeader("X-Appwrite-Session", value)
        return this
    }

    fun addHeader(key: String, value: String): Client {
        headers[key] = value
        return this
    }

    @Throws(AppwriteException::class, CancellationException::class)
    suspend inline fun<reified T> call(
        method: HttpMethod,
        path: String,
        headers: Map<String, String>,
        params: Map<String, Any>,
        responseType: ResponseType?
    ): T {
        val response = client.request {
            this.method = io.ktor.http.HttpMethod(method.nameUppercase)
            this.url {
                host = endpoint
                encodedPath = path
                params.forEach { (key, value) ->
                    parameter(key, value)
                }
            }
            (headers + this@Client.headers).forEach {
                header(it.key, it.value)
            }

            if (method == HttpMethod.Get) {
                params.forEach { (key, value) ->
                    when (value) {
                        is List<*> -> {
                            for (index in value.indices) {
                                parameter("$key[]", value[index].toString())
                            }
                        }
                        else -> {
                            parameter(key, value.toString())
                        }
                    }
                }
            } else if (headers["content-type"] == "multipart/form-data") {
                val formData = formData {
                    params.forEach {
                        when {
                            it.key == "file" -> {
                                val value = it.value as FileToUpload
                                value.channelProvider?.let { provider ->
                                    append(it.key, value.filename, value.contentType, provider)
                                } ?: run {
                                    value.inputProvider?.let { provider ->
                                        append(it.key, value.filename, value.contentType, provider)
                                    }
                                }
                            }
                            it.value is List<*> -> {
                                val list = it.value as List<*>
                                for (index in list.indices) {
                                    append("${it.key}[]", list[index].toString())
                                }
                            }
                            else -> {
                                append(it.key, it.value.toString())
                            }
                        }
                    }
                }

                setBody(MultiPartFormDataContent(formData))
            } else {
                setBody(json.encodeToString(params.toJsonElement()))
            }
        }

        if (!response.status.isSuccess()) {
            if (response.contentType()?.match(ContentType.Application.Json) == true) {
                val body: Map<String, Any?> = response.body<JsonElement>().jsonObject.toMap()
                throw AppwriteException(
                    message = body["message"] as? String ?: "",
                    code = (body["code"] as Number).toInt(),
                    type = body["type"] as? String ?: "",
                    response = json.encodeToString(body.toJsonElement())
                )
            } else {
                throw AppwriteException(
                    message = response.bodyAsText(),
                    code = response.status.value,
                )
            }
        }

        return when (responseType) {
            ResponseType.Json, ResponseType.Bytes -> response.body()
            ResponseType.Plain, null -> response.bodyAsText() as T
        }
    }

    @Throws(AppwriteException::class, CancellationException::class)
    suspend inline fun<reified T> chunkedUpload(
        path: String,
        headers:  MutableMap<String, String>,
        params: MutableMap<String, Any>,
        paramName: String,
        responseType: ResponseType?,
        idParamName: String? = null,
        onProgress: ((UploadProgress) -> Unit) = {},
    ): T {
        lateinit var file: Path
        val input = params[paramName] as InputFile
        val size: Long = when (input.sourceType) {
            "path", "file" -> {
                file = Path(input.path)
                SystemFileSystem.metadataOrNull(file)?.size ?: 0L
            }
            "bytes" -> {
                (input.data as ByteArray).size.toLong()
            }
            else -> throw UnsupportedOperationException()
        }

        if (size < CHUNK_SIZE) {
            val data = when (input.sourceType) {
                "file", "path" -> {
                    val metadata = SystemFileSystem.metadataOrNull(file) ?: throw IllegalStateException()
                    FileToUpload(file.name, ContentType.parse(input.mimeType), inputProvider = InputProvider(metadata.size) { FileReadInput(
                        SystemFileSystem.source(file).buffered()
                    ) })
                }
                "bytes" -> {
                    val data = input.data as ByteArray
                    FileToUpload(file.name, ContentType.parse(input.mimeType), channelProvider = ChannelProvider(data.size.toLong()) { ByteReadChannel(data) })
                }
                else -> throw UnsupportedOperationException()
            }

            params[paramName] = data
            return call(
                HttpMethod.Post,
                path,
                headers,
                params,
                responseType
            )
        }

        val buffer = ByteArray(CHUNK_SIZE)
        var offset = 0L
        var result: Map<*, *>? = null

        if (idParamName?.isNotEmpty() == true && params[idParamName] != "unique()") {
            // Make a request to check if a file already exists
            val current: Map<*, *> = call<JsonElement>(
                method = HttpMethod.Get,
                path = "$path/${params[idParamName]}",
                headers = headers,
                params = emptyMap(),
                responseType = ResponseType.Json
            ).jsonObject.toMap()
            val chunksUploaded = current["chunksUploaded"] as Long
            offset = chunksUploaded * CHUNK_SIZE
        }

        val source = SystemFileSystem.source(file).buffered()

        while (offset < size) {
            val data = when(input.sourceType) {
                "file", "path" -> {
                    val read = source.readAtMostTo(buffer)
                    if (read <= 0) throw IllegalStateException("Failed to read from file")
                    FileToUpload(file.name, ContentType.parse(input.mimeType), channelProvider = ChannelProvider(buffer.size.toLong()) { ByteReadChannel(buffer) })
                }
                "bytes" -> {
                    val end = if (offset + CHUNK_SIZE < size) {
                        offset + CHUNK_SIZE - 1
                    } else {
                        size - 1
                    }
                    (input.data as ByteArray).copyInto(
                        buffer,
                        startIndex = offset.toInt(),
                        endIndex = end.toInt()
                    )
                    FileToUpload(file.name, ContentType.parse(input.mimeType), channelProvider = ChannelProvider(buffer.size.toLong()) { ByteReadChannel(buffer) })
                }
                else -> throw UnsupportedOperationException()
            }

            params[paramName] = data
            headers["Content-Range"] =
                "bytes $offset-${((offset + CHUNK_SIZE) - 1).coerceAtMost(size - 1)}/$size"

            result = call<JsonElement>(
                method = HttpMethod.Post,
                path,
                headers,
                params,
                responseType = ResponseType.Json
            ).jsonObject.toMap()

            offset += CHUNK_SIZE
            headers["x-appwrite-id"] = result["\$id"].toString()
            onProgress.invoke(
                UploadProgress(
                    id = result["\$id"].toString(),
                    progress = offset.coerceAtMost(size).toDouble() / size * 100,
                    sizeUploaded = offset.coerceAtMost(size),
                    chunksTotal = result["chunksTotal"].toString().toInt(),
                    chunksUploaded = result["chunksUploaded"].toString().toInt(),
                )
            )
        }

        return json.decodeFromJsonElement(result.toJsonElement())
    }

    companion object {
        const val CHUNK_SIZE = 5*1024*1024; // 5MB
    }
}

expect fun createSettings(): Settings

expect fun createHttpClient(selfSigned: Boolean = false, block: HttpClientConfig<*>.() -> Unit = {}): HttpClient

fun FormBuilder.append(
    key: String,
    filename: String,
    contentType: ContentType? = null,
    provider: ChannelProvider
) {
    val headersBuilder = HeadersBuilder()
    headersBuilder[HttpHeaders.ContentDisposition] = "filename=${filename.escapeIfNeeded()}"
    contentType?.run { headersBuilder[HttpHeaders.ContentType] = this.toString() }
    val headers = headersBuilder.build()

    append(key, provider, headers)
}

fun FormBuilder.append(
    key: String,
    filename: String,
    contentType: ContentType? = null,
    provider: InputProvider
) {
    val headersBuilder = HeadersBuilder()
    headersBuilder[HttpHeaders.ContentDisposition] = "filename=${filename.escapeIfNeeded()}"
    contentType?.run { headersBuilder[HttpHeaders.ContentType] = this.toString() }
    val headers = headersBuilder.build()

    append(key, provider, headers)
}