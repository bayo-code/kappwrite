package com.bayocode.kappwrite.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Teams List
 */
@Serializable
data class TeamList<T>(
    /**
     * Total number of teams documents that matched your query.
     */
    @SerialName("total")
    val total: Long,

    /**
     * List of teams.
     */
    @SerialName("teams")
    val teams: List<Team<T>>,

) {
    fun toMap(): Map<String, Any> = mapOf(
        "total" to total as Any,
        "teams" to teams.map { it.toMap() } as Any,
    )
}