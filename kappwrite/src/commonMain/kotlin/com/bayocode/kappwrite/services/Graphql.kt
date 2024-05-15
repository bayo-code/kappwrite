package com.bayocode.kappwrite.services

import com.bayocode.kappwrite.Client
import com.bayocode.kappwrite.HttpMethod
import com.bayocode.kappwrite.ResponseType
import com.bayocode.kappwrite.Service

/**
 * The GraphQL API allows you to query and mutate your Appwrite server using GraphQL.
**/
class Graphql(client: Client) : Service(client) {

    /**
     * GraphQL endpoint
     *
     * Execute a GraphQL mutation.
     *
     * @param query The query or queries to execute.
     * @return [Any]
     */
    suspend fun query(
        query: Any,
    ): Any {
        val apiPath = "/graphql"

        val apiParams = mutableMapOf<String, Any?>(
            "query" to query,
        )
        val apiHeaders = mutableMapOf(
            "x-sdk-graphql" to "true",
            "content-type" to "application/json",
        )
        return client.call(
            HttpMethod.Post,
            apiPath,
            apiHeaders,
            apiParams.filterValues { it != null }.mapValues { it.value!! },
            responseType = ResponseType.Json
        )
    }


    /**
     * GraphQL endpoint
     *
     * Execute a GraphQL mutation.
     *
     * @param query The query or queries to execute.
     * @return [Any]
     */
    suspend fun mutation(
        query: Any,
    ): Any {
        val apiPath = "/graphql/mutation"

        val apiParams = mutableMapOf<String, Any?>(
            "query" to query,
        )
        val apiHeaders = mutableMapOf(
            "x-sdk-graphql" to "true",
            "content-type" to "application/json",
        )
        return client.call(
            HttpMethod.Post,
            apiPath,
            apiHeaders,
            apiParams.filterValues { it != null }.mapValues { it.value!! },
            responseType = ResponseType.Json
        )
    }


}