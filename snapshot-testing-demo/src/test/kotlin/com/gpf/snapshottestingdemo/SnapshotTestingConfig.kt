package com.gpf.snapshottestingdemo

import okhttp3.OkHttpClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class SnapshotTestingConfig {
    private val okHttpRecordResponseInterceptor = OkHttpRecordResponseInterceptor()

    @Bean
    @Primary
    fun mockHttpClient(okHttpClient: OkHttpClient) =
        okHttpClient.newBuilder().addInterceptor(okHttpRecordResponseInterceptor).build()
}
