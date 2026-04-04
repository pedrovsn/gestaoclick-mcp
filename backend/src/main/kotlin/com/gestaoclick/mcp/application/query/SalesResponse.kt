package com.gestaoclick.mcp.application.query

import com.fasterxml.jackson.annotation.JsonInclude

data class SalesResponse(
    val period: PeriodResponse,
    val filters: FiltersResponse,
    val total: Int,
    val sales: List<SaleResponse>
)

data class PeriodResponse(
    val dataInicio: String,
    val dataFim: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FiltersResponse(
    val lojaId: Int? = null,
    val codigo: Int? = null,
    val nome: String? = null,
    val situacaoId: Int? = null,
    val clienteId: Int? = null,
    val centroCustoId: Int? = null
)

data class SaleResponse(
    val id: String?,
    val codigo: String?,
    val clienteId: String?,
    val nomeCliente: String?,
    val nomeVendedor: String?,
    val data: String?,
    val nomeSituacao: String?,
    val valorTotal: String?,
    val nomeLoja: String?,
    val pagamentos: List<PaymentResponse>,
    val produtos: List<ProductResponse>,
    val servicos: List<ServiceResponse>
)

data class PaymentResponse(
    val dataVencimento: String?,
    val valor: String?,
    val nomeFormaPagamento: String?,
    val nomePlanoConta: String?,
    val observacao: String?
)

data class ProductResponse(
    val produtoId: Long?,
    val nomeProduto: String?,
    val quantidade: String?,
    val valorVenda: String?,
    val valorTotal: String?
)

data class ServiceResponse(
    val servicoId: String?,
    val nomeServico: String?,
    val quantidade: String?,
    val valorVenda: String?,
    val valorTotal: String?
)
