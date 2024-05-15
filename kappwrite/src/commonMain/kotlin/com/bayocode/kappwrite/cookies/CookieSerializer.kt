package com.bayocode.kappwrite.cookies

import io.ktor.http.Cookie
import io.ktor.http.CookieEncoding
import io.ktor.util.date.GMTDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.descriptors.mapSerialDescriptor
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.serializer

object CookieSerializer: KSerializer<Cookie> {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor(serialName = "io.ktor.http.Cookie") {
            element<String>("name")
            element<String>("value")
            element<CookieEncoding>("encoding")
            element<Int>("maxAge")
            element("expires", serialDescriptor<GMTDate>().nullable)
            element("domain", serialDescriptor<String>().nullable)
            element("path", serialDescriptor<String>().nullable)
            element<Boolean>("secure")
            element<Boolean>("httpOnly")
            element("extensions", mapSerialDescriptor<String, String?>())
        }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): Cookie = decoder.decodeStructure(descriptor) {
        var name: String? = null
        var value: String? = null
        var encoding: CookieEncoding? = null
        var maxAge: Int? = null
        var expires: GMTDate? = null
        var domain: String? = null
        var path: String? = null
        var secure: Boolean? = null
        var httpOnly: Boolean? = null
        var extensions: Map<String, String?>? = null

        while (true) {
            when (val index = decodeElementIndex(descriptor)) {
                0 -> name = decodeStringElement(descriptor, index)
                1 -> value = decodeStringElement(descriptor, index)
                2 -> encoding = decodeSerializableElement(descriptor, index, serializer<CookieEncoding>())
                3 -> maxAge = decodeIntElement(descriptor, index)
                4 -> expires = decodeNullableSerializableElement(descriptor, index, serializer<GMTDate>())
                5 -> domain = decodeNullableSerializableElement(descriptor, index, String.serializer())
                6 -> path = decodeNullableSerializableElement(descriptor, index, String.serializer())
                7 -> secure = decodeBooleanElement(descriptor, index)
                8 -> httpOnly = decodeBooleanElement(descriptor, index)
                9 -> extensions = decodeSerializableElement(descriptor, index, serializer())
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }

        Cookie(
            name = name!!,
            value = value!!,
            encoding = encoding!!,
            maxAge = maxAge!!,
            expires = expires,
            domain = domain,
            path = path,
            secure = secure!!,
            httpOnly = httpOnly!!,
            extensions = extensions!!
        )
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Cookie) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.name)
            encodeStringElement(descriptor, 1, value.value)
            encodeSerializableElement(descriptor, 2, serializer<CookieEncoding>(), value.encoding)
            encodeIntElement(descriptor, 3, value.maxAge)
            encodeNullableSerializableElement(descriptor, 4, serializer<GMTDate>(), value.expires)
            encodeNullableSerializableElement(descriptor, 5, String.serializer(), value.domain)
            encodeNullableSerializableElement(descriptor, 6, String.serializer(), value.path)
            encodeBooleanElement(descriptor, 7, value.secure)
            encodeBooleanElement(descriptor, 8, value.httpOnly)
            encodeSerializableElement(descriptor, 9, serializer(), value.extensions)
        }
    }
}

object CookieEncodingSerializer: KSerializer<CookieEncoding> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("io.ktor.http.CookieEncoding", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): CookieEncoding {
        return decoder.decodeInt().let { value ->
            CookieEncoding.values().first { it.ordinal == value }
        }
    }

    override fun serialize(encoder: Encoder, value: CookieEncoding) {
        encoder.encodeInt(value.ordinal)
    }

}

object GMTDateSerializer: KSerializer<GMTDate> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("io.ktor.util.date.GMTDate", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): GMTDate {
        return GMTDate(decoder.decodeLong())
    }

    override fun serialize(encoder: Encoder, value: GMTDate) {
        encoder.encodeLong(value.timestamp)
    }
}

internal val serializers = SerializersModule {
    contextual(CookieSerializer)
    contextual(CookieEncodingSerializer)
    contextual(GMTDateSerializer)
}
