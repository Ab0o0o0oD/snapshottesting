package com.gpf.snapshottestingdemo

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Paths

class OkHttpRecordResponseInterceptor : Interceptor {
    private val format = Json { prettyPrint = true }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val requestBodyString = getRequestBodyAsStringOrNull(request)

        val directory = Paths.get("snapshot-inputs").toFile()
        directory.mkdirs() // ensure dir exists

        val filename = generateFilename(request, requestBodyString)

        LOGGER.info("Replaying request ${request.method} ${request.url} from $directory/$filename")

        val file = directory.resolve(filename)

        return try {
            if (file.exists()) {
                LOGGER.info("Found recorded result at $directory/$filename")
                responseBasedOnRecording(request, file)
            } else {
                LOGGER.info("No recorded result found at $directory/$filename")
                executeRequestAndRecordResponse(chain, request, requestBodyString, file)
            }
        } catch (e: Exception) {
            LOGGER.error("Failed to read replayed proxy or execute request. ${e.message}", e)
            throw e
        }
    }

    private fun getRequestBodyAsStringOrNull(request: Request): String? =
        request.body?.let { requestBody ->
            val buffer = Buffer()
            requestBody.writeTo(buffer)

            buffer.readString(StandardCharsets.UTF_8)
        }

    private fun generateFilename(request: Request, requestBodyString: String?): String {
        val requestBodyPart = requestBodyString?.let { "_" + it.hashCode().toString() } ?: ""

        return "${request.method}" +
            "${request.url.encodedPath.replace("/", "_")}" +
            "${request.url.encodedQuery.orEmpty()}" +
            "$requestBodyPart.json"
    }

    private fun responseBasedOnRecording(request: Request, file: File): Response {
        val recordedResponse = format.decodeFromString<OkHttpRecordedResponse>(file.readText())

        val body = (recordedResponse.body ?: "").toResponseBody(
            recordedResponse.mediatype?.let { recordedResponse.mediatype.toMediaType() }
        )

        return Response.Builder()
            .request(request)
            .code(recordedResponse.statusCode)
            .message(recordedResponse.message)
            .protocol(Protocol.HTTP_1_1)
            .body(body)
            .build()
    }

    private fun executeRequestAndRecordResponse(
        chain: Interceptor.Chain,
        request: Request,
        requestBodyString: String?,
        file: File
    ): Response {
        val response = chain.proceed(request)

        if (response.isSuccessful) {
            recordResponse(request, requestBodyString, response, file)
        } else {
            LOGGER.error("Request failed with status code ${response.code}, not storing any recording!")
        }
        return response
    }

    private fun recordResponse(request: Request, requestBodyString: String?, response: Response, file: File) {
        val mediaType = response.body?.contentType()
        val body = mediaType?.let { response.peekBody(Long.MAX_VALUE).string() }

        val recordedRequest = request.body?.let { requestBody ->
            OkHttpRecordedRequest(
                mediatype = requestBody.contentType()?.toString(),
                body = requestBodyString.toString()
            )
        }

        file.writeText(
            format.encodeToString(
                OkHttpRecordedResponse(
                    statusCode = response.code,
                    message = response.message,
                    mediatype = mediaType?.toString(),
                    body = body,
                    request = recordedRequest
                )
            )
        )
    }

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val LOGGER = LoggerFactory.getLogger(javaClass.enclosingClass)
    }
}

@Serializable
data class OkHttpRecordedResponse(
    val statusCode: Int,
    val message: String,
    val mediatype: String?,
    val body: String?,
    val request: OkHttpRecordedRequest? = null
)

@Serializable
data class OkHttpRecordedRequest(
    val mediatype: String?,
    val body: String?
)
