package com.gestaoclick.mcp.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
@EnableConfigurationProperties(ErpProperties::class)
class ErpConfig {

    @Bean
    fun erpRestTemplate(properties: ErpProperties): RestTemplate {
        val factory = SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(properties.timeoutMs)
            setReadTimeout(properties.timeoutMs)
        }
        return RestTemplate(factory)
    }
}