package com.bayocode.kappwrite.extensions

import com.bayocode.kappwrite.json
import com.bayocode.kappwrite.toJsonElement
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

fun Any.toJson(): String = json.encodeToString(toJsonElement())

@OptIn(InternalSerializationApi::class)
fun <T: Any> String.fromJson(clazz: KClass<T>): T = json.decodeFromString(clazz.serializer(), this)

inline fun <reified T> String.fromJson(): T = json.decodeFromString(this)