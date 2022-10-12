package com.gpf.snapshottestingdemo.config

import no.gjensidige.gpf.rest.client.RestClientBuilder
import no.gjensidige.gpf.rest.client.RestClientConfig
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Import(
    RestClientConfig::class
)
@Configuration
class AppConfig {

    @Bean("http-client-template-demo")
    fun okHttpClient(
        restClientBuilder: RestClientBuilder
    ): OkHttpClient = restClientBuilder.builder().build()
}
