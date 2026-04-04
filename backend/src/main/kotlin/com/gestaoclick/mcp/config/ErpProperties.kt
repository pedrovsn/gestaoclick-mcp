package com.gestaoclick.mcp.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "erp")
data class ErpProperties(
    val baseUrl: String,
    val accessToken: String,
    val secretAccessToken: String,
    val timeoutMs: Int = 5000
)