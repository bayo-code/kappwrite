package com.bayocode.kappwrite.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Documents List
 */
@Serializable
data class DocumentList<T>(
    /**
     * Total number of documents documents that matched your query.
     */
    @SerialName("total")
    val total: Long,

    /**
     * List of documents.
     */
    @SerialName("documents")
    val documents: List<Document<T>>,

) {
    fun toMap(): Map<String, Any> = mapOf(
        "total" to total as Any,
        "documents" to documents.map { it.toMap() } as Any,
    )
}