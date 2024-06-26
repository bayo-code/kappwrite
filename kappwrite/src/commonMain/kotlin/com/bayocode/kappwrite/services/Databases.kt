package com.bayocode.kappwrite.services

import com.bayocode.kappwrite.Client
import com.bayocode.kappwrite.HttpMethod
import com.bayocode.kappwrite.ResponseType
import com.bayocode.kappwrite.Service
import kotlin.jvm.JvmOverloads
import com.bayocode.kappwrite.models.*

/**
 * The Databases service allows you to create structured collections of documents, query and filter lists of documents
**/
class Databases(client: Client) : Service(client) {

    /**
     * List documents
     *
     * Get a list of all the user&#039;s documents in a given collection. You can use the query params to filter your results.
     *
     * @param databaseId Database ID.
     * @param collectionId Collection ID. You can create a new collection using the Database service [server integration](https://appwrite.io/docs/server/databases#databasesCreateCollection).
     * @param queries Array of query strings generated using the Query class provided by the SDK. [Learn more about queries](https://appwrite.io/docs/queries). Maximum of 100 queries are allowed, each 4096 characters long.
     * @return [DocumentList<T>]
     */
    @JvmOverloads
    suspend fun listDocuments(
        databaseId: String,
        collectionId: String,
        queries: List<String>? = null,
    ): DocumentList {
        val apiPath = "/databases/{databaseId}/collections/{collectionId}/documents"
            .replace("{databaseId}", databaseId)
            .replace("{collectionId}", collectionId)

        val apiParams = mutableMapOf<String, Any?>(
            "queries" to queries,
        )
        val apiHeaders = mutableMapOf(
            "content-type" to "application/json",
        )
        return client.call(
            HttpMethod.Get,
            apiPath,
            apiHeaders,
            apiParams.filterValues { it != null }.mapValues { it.value!! },
            responseType = ResponseType.Bytes,
        )
    }

    /**
     * Create document
     *
     * Create a new Document. Before using this route, you should create a new collection resource using either a [server integration](https://appwrite.io/docs/server/databases#databasesCreateCollection) API or directly from your database console.
     *
     * @param databaseId Database ID.
     * @param collectionId Collection ID. You can create a new collection using the Database service [server integration](https://appwrite.io/docs/server/databases#databasesCreateCollection). Make sure to define attributes before creating documents.
     * @param documentId Document ID. Choose a custom ID or generate a random ID with `ID.unique()`. Valid chars are a-z, A-Z, 0-9, period, hyphen, and underscore. Can't start with a special char. Max length is 36 chars.
     * @param data Document data as JSON object.
     * @param permissions An array of permissions strings. By default, only the current user is granted all permissions. [Learn more about permissions](https://appwrite.io/docs/permissions).
     * @return [Document<T>]
     */
    @JvmOverloads
    suspend fun createDocument(
        databaseId: String,
        collectionId: String,
        documentId: String,
        data: Any,
        permissions: List<String>? = null,
    ): Document {
        val apiPath = "/databases/{databaseId}/collections/{collectionId}/documents"
            .replace("{databaseId}", databaseId)
            .replace("{collectionId}", collectionId)

        val apiParams = mutableMapOf<String, Any?>(
            "documentId" to documentId,
            "data" to data,
            "permissions" to permissions,
        )
        val apiHeaders = mutableMapOf(
            "content-type" to "application/json",
        )
        return client.call(
            HttpMethod.Post,
            apiPath,
            apiHeaders,
            apiParams.filterValues { it != null }.mapValues { it.value!! },
            responseType = ResponseType.Json,
        )
    }

    /**
     * Get document
     *
     * Get a document by its unique ID. This endpoint response returns a JSON object with the document data.
     *
     * @param databaseId Database ID.
     * @param collectionId Collection ID. You can create a new collection using the Database service [server integration](https://appwrite.io/docs/server/databases#databasesCreateCollection).
     * @param documentId Document ID.
     * @param queries Array of query strings generated using the Query class provided by the SDK. [Learn more about queries](https://appwrite.io/docs/queries). Maximum of 100 queries are allowed, each 4096 characters long.
     * @return [Document<T>]
     */
    @JvmOverloads
    suspend fun getDocument(
        databaseId: String,
        collectionId: String,
        documentId: String,
        queries: List<String>? = null,
    ): Document {
        val apiPath = "/databases/{databaseId}/collections/{collectionId}/documents/{documentId}"
            .replace("{databaseId}", databaseId)
            .replace("{collectionId}", collectionId)
            .replace("{documentId}", documentId)

        val apiParams = mutableMapOf<String, Any?>(
            "queries" to queries,
        )
        val apiHeaders = mutableMapOf(
            "content-type" to "application/json",
        )
        return client.call(
            HttpMethod.Get,
            apiPath,
            apiHeaders,
            apiParams.filterValues { it != null }.mapValues { it.value!! },
            responseType = ResponseType.Json,
        )
    }

    /**
     * Update document
     *
     * Update a document by its unique ID. Using the patch method you can pass only specific fields that will get updated.
     *
     * @param databaseId Database ID.
     * @param collectionId Collection ID.
     * @param documentId Document ID.
     * @param data Document data as JSON object. Include only attribute and value pairs to be updated.
     * @param permissions An array of permissions strings. By default, the current permissions are inherited. [Learn more about permissions](https://appwrite.io/docs/permissions).
     * @return [Document<T>]
     */
    @JvmOverloads
    suspend fun updateDocument(
        databaseId: String,
        collectionId: String,
        documentId: String,
        data: Any? = null,
        permissions: List<String>? = null,
    ): Document {
        val apiPath = "/databases/{databaseId}/collections/{collectionId}/documents/{documentId}"
            .replace("{databaseId}", databaseId)
            .replace("{collectionId}", collectionId)
            .replace("{documentId}", documentId)

        val apiParams = mutableMapOf<String, Any?>(
            "data" to data,
            "permissions" to permissions,
        )
        val apiHeaders = mutableMapOf(
            "content-type" to "application/json",
        )
        return client.call(
            HttpMethod.Patch,
            apiPath,
            apiHeaders,
            apiParams.filterValues { it != null }.mapValues { it.value!! },
            responseType = ResponseType.Json,
        )
    }

    /**
     * Delete document
     *
     * Delete a document by its unique ID.
     *
     * @param databaseId Database ID.
     * @param collectionId Collection ID. You can create a new collection using the Database service [server integration](https://appwrite.io/docs/server/databases#databasesCreateCollection).
     * @param documentId Document ID.
     * @return [Any]
     */
    suspend fun deleteDocument(
        databaseId: String,
        collectionId: String,
        documentId: String,
    ): Any {
        val apiPath = "/databases/{databaseId}/collections/{collectionId}/documents/{documentId}"
            .replace("{databaseId}", databaseId)
            .replace("{collectionId}", collectionId)
            .replace("{documentId}", documentId)

        val apiParams = mutableMapOf<String, Any?>(
        )
        val apiHeaders = mutableMapOf(
            "content-type" to "application/json",
        )
        return client.call(
            HttpMethod.Delete,
            apiPath,
            apiHeaders,
            apiParams.filterValues { it != null }.mapValues { it.value!! },
            responseType = ResponseType.Json
        )
    }


}