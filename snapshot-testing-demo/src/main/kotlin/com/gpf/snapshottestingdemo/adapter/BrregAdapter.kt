package com.gpf.snapshottestingdemo.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.gpf.snapshottestingdemo.domain.Company
import no.gjensidige.gpf.rest.client.executeAndRead
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Component

@Component
class BrregAdapter(
    private val httpClient: OkHttpClient,
    private val objectMapper: ObjectMapper
) {

    fun getCompany(org: Int): Result<Company> {
        val request = Request.Builder()
            .url("https://data.brreg.no/enhetsregisteret/api/enheter/$org")
            .get()
            .build()

        return httpClient.newCall(request)
            .executeAndRead(objectMapper, { response -> throw RuntimeException(response.message) }) { dto: Company? -> Result.success(dto!!) }
    }
}
