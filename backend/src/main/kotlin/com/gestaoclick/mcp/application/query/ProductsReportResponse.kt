package com.gestaoclick.mcp.application.query

import com.fasterxml.jackson.annotation.JsonInclude

data class ProductsReportResponse(
    val filters: ProductsReportFiltersResponse,
    val summary: ProductsReportSummaryResponse,
    val total: Int,
    val products: List<ProductItemResponse>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ProductsReportFiltersResponse(
    val nome: String? = null,
    val ativo: String? = null,
    val grupoId: String? = null
)

data class ProductsReportSummaryResponse(
    val totalProducts: Int,
    val totalActiveProducts: Int,
    val totalInactiveProducts: Int,
    val totalStock: Long,
    val totalCostValue: String,
    val totalSaleValue: String
)
