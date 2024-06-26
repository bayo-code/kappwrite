package com.bayocode.kappwrite.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Files List
 */
@Serializable
data class FileList(
    /**
     * Total number of files documents that matched your query.
     */
    @SerialName("total")
    val total: Long,

    /**
     * List of files.
     */
    @SerialName("files")
    val files: List<File>,

) {
    fun toMap(): Map<String, Any> = mapOf(
        "total" to total as Any,
        "files" to files.map { it.toMap() } as Any,
    )

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun from(
            map: Map<String, Any>,
        ) = FileList(
            total = (map["total"] as Number).toLong(),
            files = (map["files"] as List<Map<String, Any>>).map { File.from(map = it) },
        )
    }
}