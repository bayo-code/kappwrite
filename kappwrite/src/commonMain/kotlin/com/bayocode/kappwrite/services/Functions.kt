package com.bayocode.kappwrite.services

import com.bayocode.kappwrite.Client
import com.bayocode.kappwrite.HttpMethod
import com.bayocode.kappwrite.ResponseType
import com.bayocode.kappwrite.Service
import com.bayocode.kappwrite.enums.ExecutionMethod
import kotlin.jvm.JvmOverloads

import com.bayocode.kappwrite.models.*

/**
 * The Functions Service allows you view, create and manage your Cloud Functions.
**/
class Functions(client: Client) : Service(client) {

    /**
     * List executions
     *
     * Get a list of all the current user function execution logs. You can use the query params to filter your results.
     *
     * @param functionId Function ID.
     * @param queries Array of query strings generated using the Query class provided by the SDK. [Learn more about queries](https://appwrite.io/docs/queries). Maximum of 100 queries are allowed, each 4096 characters long. You may filter on the following attributes: trigger, status, responseStatusCode, duration
     * @param search Search term to filter your list results. Max length: 256 chars.
     * @return [ExecutionList]
     */
    @JvmOverloads
    suspend fun listExecutions(
        functionId: String,
        queries: List<String>? = null,
        search: String? = null,
    ): ExecutionList {
        val apiPath = "/functions/{functionId}/executions"
            .replace("{functionId}", functionId)

        val apiParams = mutableMapOf<String, Any?>(
            "queries" to queries,
            "search" to search,
        )
        val apiHeaders = mutableMapOf(
            "content-type" to "application/json",
        )
        return client.call(
            HttpMethod.Get,
            apiPath,
            apiHeaders,
            apiParams.filterValues { it != null }.mapValues { it.value!! },
            responseType = ResponseType.Json
        )
    }


    /**
     * Create execution
     *
     * Trigger a function execution. The returned object will return you the current execution status. You can ping the `Get Execution` endpoint to get updates on the current execution status. Once this endpoint is called, your function execution process will start asynchronously.
     *
     * @param functionId Function ID.
     * @param body HTTP body of execution. Default value is empty string.
     * @param async Execute code in the background. Default value is false.
     * @param path HTTP path of execution. Path can include query params. Default value is /
     * @param method HTTP method of execution. Default value is GET.
     * @param headers HTTP headers of execution. Defaults to empty.
     * @return [Execution]
     */
    @JvmOverloads
    suspend fun createExecution(
        functionId: String,
        body: String? = null,
        async: Boolean? = null,
        path: String? = null,
        method: ExecutionMethod? = null,
        headers: Any? = null,
    ): Execution {
        val apiPath = "/functions/{functionId}/executions"
            .replace("{functionId}", functionId)

        val apiParams = mutableMapOf<String, Any?>(
            "body" to body,
            "async" to async,
            "path" to path,
            "method" to method,
            "headers" to headers,
        )
        val apiHeaders = mutableMapOf(
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
     * Get execution
     *
     * Get a function execution log by its unique ID.
     *
     * @param functionId Function ID.
     * @param executionId Execution ID.
     * @return [Execution]
     */
    suspend fun getExecution(
        functionId: String,
        executionId: String,
    ): Execution {
        val apiPath = "/functions/{functionId}/executions/{executionId}"
            .replace("{functionId}", functionId)
            .replace("{executionId}", executionId)

        val apiParams = mutableMapOf<String, Any?>(
        )
        val apiHeaders = mutableMapOf(
            "content-type" to "application/json",
        )
        return client.call(
            HttpMethod.Get,
            apiPath,
            apiHeaders,
            apiParams.filterValues { it != null }.mapValues { it.value!! },
            responseType = ResponseType.Json
        )
    }


}