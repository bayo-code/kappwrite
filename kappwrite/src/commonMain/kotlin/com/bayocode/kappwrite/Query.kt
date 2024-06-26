@file:UseSerializers(AnySerializer::class)
package com.bayocode.kappwrite

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.encodeToString

@Serializable
class Query(
    val method: String,
    val attribute: String? = null,
    val values: List<@Contextual Any>? = null,
) {
    override fun toString() = this.toJson()
    fun toJson(): String = json.encodeToString(this)

    companion object {
        fun equal(attribute: String, value: Any) = Query("equal", attribute, parseValue(value)).toJson()

        fun notEqual(attribute: String, value: Any) = Query("notEqual", attribute, parseValue(value)).toJson()

        fun lessThan(attribute: String, value: Any) = Query("lessThan", attribute, parseValue(value)).toJson()

        fun lessThanEqual(attribute: String, value: Any) = Query("lessThanEqual", attribute, parseValue(value)).toJson()

        fun greaterThan(attribute: String, value: Any) = Query("greaterThan", attribute, parseValue(value)).toJson()

        fun greaterThanEqual(attribute: String, value: Any) = Query("greaterThanEqual", attribute, parseValue(value)).toJson()

        fun search(attribute: String, value: String) = Query("search", attribute, listOf(value)).toJson()

        fun isNull(attribute: String) = Query("isNull", attribute).toJson()

        fun isNotNull(attribute: String) = Query("isNotNull", attribute).toJson()

        fun between(attribute: String, start: Any, end: Any) = Query("between", attribute, listOf(start, end)).toJson()

        fun startsWith(attribute: String, value: String) = Query("startsWith", attribute, listOf(value)).toJson()

        fun endsWith(attribute: String, value: String) = Query("endsWith", attribute, listOf(value)).toJson()

        fun select(attributes: List<String>) = Query("select", null, attributes).toJson()

        fun orderAsc(attribute: String) = Query("orderAsc", attribute).toJson()

        fun orderDesc(attribute: String) = Query("orderDesc", attribute).toJson()

        fun cursorBefore(documentId: String) = Query("cursorBefore", null, listOf(documentId)).toJson()

        fun cursorAfter(documentId: String) = Query("cursorAfter", null, listOf(documentId)).toJson()

        fun limit(limit: Int) = Query("limit", null, listOf(limit)).toJson()

        fun offset(offset: Int) = Query("offset", null, listOf(offset)).toJson()

        fun contains(attribute: String, value: Any) = Query("contains", attribute, parseValue(value)).toJson()

        fun or(queries: List<String>) = Query("or", null, queries.map { json.decodeFromString<Query>(it) }).toJson()

        fun and(queries: List<String>) = Query("and", null, queries.map { json.decodeFromString<Query>(it) }).toJson()

        private fun parseValue(value: Any): List<Any> {
            return when (value) {
                is List<*> -> value as List<Any>
                else -> listOf(value)
            }
        }
    }
}