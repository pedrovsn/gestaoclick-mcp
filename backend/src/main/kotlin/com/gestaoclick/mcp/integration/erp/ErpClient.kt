package com.gestaoclick.mcp.integration.erp

import com.gestaoclick.mcp.config.ErpProperties
import com.gestaoclick.mcp.exception.ErpIntegrationException
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Component
class ErpClient(
    private val erpRestTemplate: RestTemplate,
    private val properties: ErpProperties
) {

    fun <T : Any> get(path: String, responseType: Class<T>, params: Map<String, String?> = emptyMap()): T {
        val uri = buildUri(path, params)
        val entity = HttpEntity<Nothing>(buildHeaders())

        return try {
            erpRestTemplate.exchange(uri, HttpMethod.GET, entity, responseType).body
                ?: throw ErpIntegrationException("Empty response from ERP at $path")
        } catch (ex: HttpClientErrorException) {
            throw ErpIntegrationException(
                "ERP client error at $path: ${ex.statusCode} - ${ex.responseBodyAsString}", ex
            )
        } catch (ex: HttpServerErrorException) {
            throw ErpIntegrationException(
                "ERP server error at $path: ${ex.statusCode} - ${ex.responseBodyAsString}", ex
            )
        } catch (ex: ResourceAccessException) {
            throw ErpIntegrationException("ERP unreachable at $path: ${ex.message}", ex)
        }
    }

    private fun buildUri(path: String, params: Map<String, String?>): URI {
        val builder = UriComponentsBuilder
            .fromUriString("${properties.baseUrl.trimEnd('/')}/$path")

        params.forEach { (key, value) ->
            if (value != null) builder.queryParam(key, value)
        }

        return builder.build().toUri()
    }

    private fun buildHeaders(): HttpHeaders = HttpHeaders().apply {
        contentType = MediaType.APPLICATION_JSON
        set("access-token", properties.accessToken)
        set("secret-access-token", properties.secretAccessToken)
    }
}
