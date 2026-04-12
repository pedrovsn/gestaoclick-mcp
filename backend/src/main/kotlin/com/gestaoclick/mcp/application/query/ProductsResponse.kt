package com.gestaoclick.mcp.application.query

import com.fasterxml.jackson.annotation.JsonInclude

data class ProductsListResponse(
    val filters: ProductsFiltersResponse,
    val total: Int,
    val products: List<ProductItemResponse>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ProductsFiltersResponse(
    val nome: String? = null,
    val ativo: String? = null,
    val grupoId: String? = null
)

data class ProductItemResponse(
    val id: String?,
    val nome: String?,
    val codigoInterno: String?,
    val codigoBarra: String?,
    val ativo: String?,
    val grupoId: String?,
    val nomeGrupo: String?,
    val estoque: Int?,
    val valorCusto: String?,
    val valorVenda: String?,
    val valores: List<ProductPriceItemResponse>,
    val variacoes: List<ProductVariationItemResponse>
)

data class ProductPriceItemResponse(
    val nomeTipo: String?,
    val valorCusto: String?,
    val valorVenda: String?
)

data class ProductVariationItemResponse(
    val id: String?,
    val nome: String?,
    val estoque: String?,
    val valores: List<ProductPriceItemResponse>
)
