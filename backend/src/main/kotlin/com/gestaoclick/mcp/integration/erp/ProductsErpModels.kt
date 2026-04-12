package com.gestaoclick.mcp.integration.erp

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductsListErpResponse(
    @JsonProperty("code") val code: Int?,
    @JsonProperty("status") val status: String?,
    @JsonProperty("meta") val meta: ProductsListErpMeta?,
    @JsonProperty("data") val data: List<ProductErpResponse>?
)

data class ProductsListErpMeta(
    @JsonProperty("total_registros") val totalRegistros: Int?,
    @JsonProperty("total_registros_pagina") val totalRegistrosPagina: Int?,
    @JsonProperty("pagina_atual") val paginaAtual: Int?,
    @JsonProperty("limite_por_pagina") val limitePorPagina: Int?,
    @JsonProperty("pagina_anterior") val paginaAnterior: Int?,
    @JsonProperty("url_anterior") val urlAnterior: String?,
    @JsonProperty("proxima_pagina") val proximaPagina: Int?,
    @JsonProperty("proxima_url") val proximaUrl: String?
)

data class ProductErpResponse(
    @JsonProperty("id") val id: String?,
    @JsonProperty("nome") val nome: String?,
    @JsonProperty("codigo_interno") val codigoInterno: String?,
    @JsonProperty("codigo_barra") val codigoBarra: String?,
    @JsonProperty("possui_variacao") val possuiVariacao: String?,
    @JsonProperty("possui_composicao") val possuiComposicao: String?,
    @JsonProperty("movimenta_estoque") val movimentaEstoque: String?,
    @JsonProperty("peso") val peso: String?,
    @JsonProperty("largura") val largura: String?,
    @JsonProperty("altura") val altura: String?,
    @JsonProperty("comprimento") val comprimento: String?,
    @JsonProperty("ativo") val ativo: String?,
    @JsonProperty("grupo_id") val grupoId: String?,
    @JsonProperty("nome_grupo") val nomeGrupo: String?,
    @JsonProperty("descricao") val descricao: String?,
    @JsonProperty("estoque") val estoque: Int?,
    @JsonProperty("valor_custo") val valorCusto: String?,
    @JsonProperty("valor_venda") val valorVenda: String?,
    @JsonProperty("valores") val valores: List<ProductPriceErpResponse>?,
    @JsonProperty("variacoes") val variacoes: List<ProductVariationWrapperErpResponse>?,
    @JsonProperty("fiscal") val fiscal: ProductFiscalErpResponse?
)

data class ProductPriceErpResponse(
    @JsonProperty("tipo_id") val tipoId: String?,
    @JsonProperty("nome_tipo") val nomeTipo: String?,
    @JsonProperty("lucro_utilizado") val lucroUtilizado: String?,
    @JsonProperty("valor_custo") val valorCusto: String?,
    @JsonProperty("valor_venda") val valorVenda: String?
)

data class ProductVariationWrapperErpResponse(
    @JsonProperty("variacao") val variacao: ProductVariationErpResponse?
)

data class ProductVariationErpResponse(
    @JsonProperty("id") val id: String?,
    @JsonProperty("nome") val nome: String?,
    @JsonProperty("estoque") val estoque: String?,
    @JsonProperty("valores") val valores: List<ProductPriceErpResponse>?
)

data class ProductFiscalErpResponse(
    @JsonProperty("ncm") val ncm: String?,
    @JsonProperty("cest") val cest: String?,
    @JsonProperty("peso_liquido") val pesoLiquido: String?,
    @JsonProperty("peso_bruto") val pesoBruto: String?,
    @JsonProperty("valor_aproximado_tributos") val valorAproximadoTributos: String?,
    @JsonProperty("valor_fixo_pis") val valorFixoPis: String?,
    @JsonProperty("valor_fixo_pis_st") val valorFixoPisSt: String?,
    @JsonProperty("valor_fixo_confins") val valorFixoConfins: String?,
    @JsonProperty("valor_fixo_confins_st") val valorFixoConfinsSt: String?
)
