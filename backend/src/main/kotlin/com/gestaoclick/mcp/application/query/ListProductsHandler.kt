package com.gestaoclick.mcp.application.query

import com.gestaoclick.mcp.integration.erp.ErpClient
import com.gestaoclick.mcp.integration.erp.ProductErpResponse
import com.gestaoclick.mcp.integration.erp.ProductPriceErpResponse
import com.gestaoclick.mcp.integration.erp.ProductVariationErpResponse
import com.gestaoclick.mcp.integration.erp.ProductsListErpResponse
import org.springframework.stereotype.Component

@Component
class ListProductsHandler(private val erpClient: ErpClient) {

    fun handle(query: ListProductsQuery): ProductsListResponse {
        val allProducts = mutableListOf<ProductErpResponse>()
        var page = 1

        do {
            val response = erpClient.get("produtos", ProductsListErpResponse::class.java, buildParams(query, page))
            allProducts.addAll(response.data ?: emptyList())
            val nextPage = response.meta?.proximaPagina ?: break
            page = nextPage
        } while (true)

        return ProductsListResponse(
            filters = ProductsFiltersResponse(
                nome = query.nome,
                ativo = query.ativo,
                grupoId = query.grupoId
            ),
            total = allProducts.size,
            products = allProducts.map { it.toProductItemResponse() }
        )
    }

    private fun buildParams(query: ListProductsQuery, page: Int): Map<String, String?> = mapOf(
        "pagina" to page.toString(),
        "nome" to query.nome,
        "ativo" to query.ativo,
        "grupo_id" to query.grupoId
    )
}

private fun ProductErpResponse.toProductItemResponse() = ProductItemResponse(
    id = id,
    nome = nome,
    codigoInterno = codigoInterno,
    codigoBarra = codigoBarra,
    ativo = ativo,
    grupoId = grupoId,
    nomeGrupo = nomeGrupo,
    estoque = estoque,
    valorCusto = valorCusto,
    valorVenda = valorVenda,
    valores = valores?.map { it.toPriceItemResponse() } ?: emptyList(),
    variacoes = variacoes?.mapNotNull { it.variacao?.toVariationItemResponse() } ?: emptyList()
)

private fun ProductPriceErpResponse.toPriceItemResponse() = ProductPriceItemResponse(
    nomeTipo = nomeTipo,
    valorCusto = valorCusto,
    valorVenda = valorVenda
)

private fun ProductVariationErpResponse.toVariationItemResponse() = ProductVariationItemResponse(
    id = id,
    nome = nome,
    estoque = estoque,
    valores = valores?.map { it.toPriceItemResponse() } ?: emptyList()
)
