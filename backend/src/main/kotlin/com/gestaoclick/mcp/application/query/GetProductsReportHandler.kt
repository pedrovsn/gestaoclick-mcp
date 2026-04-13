package com.gestaoclick.mcp.application.query

import com.gestaoclick.mcp.integration.erp.ErpClient
import com.gestaoclick.mcp.integration.erp.ProductErpResponse
import com.gestaoclick.mcp.integration.erp.ProductsListErpResponse
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class GetProductsReportHandler(private val erpClient: ErpClient) {

    fun handle(query: GetProductsReportQuery): ProductsReportResponse {
        val allProducts = fetchAllProducts(query)
        val items = allProducts.map { it.toReportItem() }

        return ProductsReportResponse(
            filters = ProductsReportFiltersResponse(
                nome = query.nome,
                ativo = query.ativo,
                grupoId = query.grupoId
            ),
            summary = buildSummary(allProducts),
            total = items.size,
            products = items
        )
    }

    private fun fetchAllProducts(query: GetProductsReportQuery): List<ProductErpResponse> {
        val allProducts = mutableListOf<ProductErpResponse>()
        var page = 1

        do {
            val response = erpClient.get("produtos", ProductsListErpResponse::class.java, buildParams(query, page))
            allProducts.addAll(response.data ?: emptyList())
            val nextPage = response.meta?.proximaPagina ?: break
            page = nextPage
        } while (true)

        return allProducts
    }

    private fun buildParams(query: GetProductsReportQuery, page: Int): Map<String, String?> = mapOf(
        "pagina" to page.toString(),
        "nome" to query.nome,
        "ativo" to query.ativo,
        "grupo_id" to query.grupoId
    )

    private fun buildSummary(products: List<ProductErpResponse>): ProductsReportSummaryResponse {
        val activeCount = products.count { it.ativo == "1" }
        val totalStock = products.sumOf { it.estoque?.toLong() ?: 0L }
        val totalCost = products.fold(BigDecimal.ZERO) { acc, p ->
            acc + (p.valorCusto?.toBigDecimalOrNull() ?: BigDecimal.ZERO)
        }
        val totalSale = products.fold(BigDecimal.ZERO) { acc, p ->
            acc + (p.valorVenda?.toBigDecimalOrNull() ?: BigDecimal.ZERO)
        }

        return ProductsReportSummaryResponse(
            totalProducts = products.size,
            totalActiveProducts = activeCount,
            totalInactiveProducts = products.size - activeCount,
            totalStock = totalStock,
            totalCostValue = totalCost.toPlainString(),
            totalSaleValue = totalSale.toPlainString()
        )
    }
}

private fun ProductErpResponse.toReportItem() = ProductItemResponse(
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
    valores = valores?.map {
        ProductPriceItemResponse(
            nomeTipo = it.nomeTipo,
            valorCusto = it.valorCusto,
            valorVenda = it.valorVenda
        )
    } ?: emptyList(),
    variacoes = variacoes?.mapNotNull { wrapper ->
        wrapper.variacao?.let {
            ProductVariationItemResponse(
                id = it.id,
                nome = it.nome,
                estoque = it.estoque,
                valores = it.valores?.map { price ->
                    ProductPriceItemResponse(
                        nomeTipo = price.nomeTipo,
                        valorCusto = price.valorCusto,
                        valorVenda = price.valorVenda
                    )
                } ?: emptyList()
            )
        }
    } ?: emptyList()
)
