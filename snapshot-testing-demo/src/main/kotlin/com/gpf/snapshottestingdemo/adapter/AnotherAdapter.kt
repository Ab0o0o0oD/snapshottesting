package com.gpf.snapshottestingdemo.adapter

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import no.gjensidige.gpf.rest.client.executeAndRead
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Component

@Component
class AnotherAdapter(
    val httpClient: OkHttpClient,
    val objectMapper: ObjectMapper
) {

    fun getSupervisorName(fnr: Int): Result<Users> {
        val request = Request.Builder()
            .url("https://reqres.in/api/users/$fnr")
            .get()
            .build()

        return httpClient.newCall(request).executeAndRead(objectMapper, { response ->
            throw RuntimeException(response.message)
        }) {
                dto: Users? ->
            Result.success(dto!!)
        }
    }
}

data class Supervisor(
    @JsonProperty("first_name")
    val firstName: String,
    @JsonProperty("last_name")
    val lastName: String
)
data class Users(
    val data: Supervisor
)
