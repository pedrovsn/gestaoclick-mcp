package com.gestaoclick.mcp.integration.erp

import com.fasterxml.jackson.annotation.JsonProperty

data class SalesListErpResponse(
    @JsonProperty("code") val code: Int?,
    @JsonProperty("status") val status: String?,
    @JsonProperty("meta") val meta: SalesListErpMeta?,
    @JsonProperty("data") val data: List<SaleErpResponse>?
)

data class SalesListErpMeta(
    @JsonProperty("total_registros") val totalRegistros: Int?,
    @JsonProperty("total_da_pagina") val totalDaPagina: Int?,
    @JsonProperty("pagina_atual") val paginaAtual: Int?,
    @JsonProperty("limite_por_pagina") val limitePorPagina: Int?,
    @JsonProperty("pagina_anterior") val paginaAnterior: Int?,
    @JsonProperty("url_anterior") val urlAnterior: String?,
    @JsonProperty("proxima_pagina") val proximaPagina: Int?,
    @JsonProperty("proxima_url") val proximaUrl: String?
)

data class SaleErpResponse(
    @JsonProperty("id") val id: String?,
    @JsonProperty("codigo") val codigo: String?,
    @JsonProperty("cliente_id") val clienteId: String?,
    @JsonProperty("nome_cliente") val nomeCliente: String?,
    @JsonProperty("vendedor_id") val vendedorId: String?,
    @JsonProperty("nome_vendedor") val nomeVendedor: String?,
    @JsonProperty("tecnico_id") val tecnicoId: String?,
    @JsonProperty("nome_tecnico") val nomeTecnico: String?,
    @JsonProperty("data") val data: String?,
    @JsonProperty("previsao_entrega") val previsaoEntrega: String?,
    @JsonProperty("situacao_id") val situacaoId: String?,
    @JsonProperty("nome_situacao") val nomeSituacao: String?,
    @JsonProperty("valor_total") val valorTotal: String?,
    @JsonProperty("transportadora_id") val transportadoraId: String?,
    @JsonProperty("nome_transportadora") val nomeTransportadora: String?,
    @JsonProperty("centro_custo_id") val centroCustoId: String?,
    @JsonProperty("nome_centro_custo") val nomeCentroCusto: String?,
    @JsonProperty("aos_cuidados_de") val aosCuidadosDe: String?,
    @JsonProperty("validade") val validade: String?,
    @JsonProperty("introducao") val introducao: String?,
    @JsonProperty("observacoes") val observacoes: String?,
    @JsonProperty("observacoes_interna") val observacoesInterna: String?,
    @JsonProperty("valor_frete") val valorFrete: String?,
    @JsonProperty("nome_canal_venda") val nomeCanalVenda: String?,
    @JsonProperty("nome_loja") val nomeLoja: String?,
    @JsonProperty("valor_custo") val valorCusto: String?,
    @JsonProperty("condicao_pagamento") val condicaoPagamento: String?,
    @JsonProperty("situacao_financeiro") val situacaoFinanceiro: String?,
    @JsonProperty("situacao_estoque") val situacaoEstoque: String?,
    @JsonProperty("forma_pagamento_id") val formaPagamentoId: String?,
    @JsonProperty("data_primeira_parcela") val dataPrimeiraParcela: String?,
    @JsonProperty("numero_parcelas") val numeroParcelas: String?,
    @JsonProperty("intervalo_dias") val intervaloDias: String?,
    @JsonProperty("hash") val hash: String?,
    @JsonProperty("equipamentos") val equipamentos: List<Any>?,
    @JsonProperty("pagamentos") val pagamentos: List<SalePaymentWrapperErpResponse>?,
    @JsonProperty("produtos") val produtos: List<SaleProductWrapperErpResponse>?,
    @JsonProperty("servicos") val servicos: List<SaleServiceWrapperErpResponse>?
)

data class SalePaymentWrapperErpResponse(
    @JsonProperty("pagamento") val pagamento: SalePaymentErpResponse?
)

data class SalePaymentErpResponse(
    @JsonProperty("data_vencimento") val dataVencimento: String?,
    @JsonProperty("valor") val valor: String?,
    @JsonProperty("forma_pagamento_id") val formaPagamentoId: String?,
    @JsonProperty("nome_forma_pagamento") val nomeFormaPagamento: String?,
    @JsonProperty("plano_contas_id") val planoContasId: String?,
    @JsonProperty("nome_plano_conta") val nomePlanoConta: String?,
    @JsonProperty("observacao") val observacao: String?
)

data class SaleProductWrapperErpResponse(
    @JsonProperty("produto") val produto: SaleProductErpResponse?
)

data class SaleProductErpResponse(
    @JsonProperty("produto_id") val produtoId: Long?,
    @JsonProperty("variacao_id") val variacaoId: Long?,
    @JsonProperty("nome_produto") val nomeProduto: String?,
    @JsonProperty("detalhes") val detalhes: String?,
    @JsonProperty("movimenta_estoque") val movimentaEstoque: String?,
    @JsonProperty("possui_variacao") val possuiVariacao: String?,
    @JsonProperty("sigla_unidade") val siglaUnidade: String?,
    @JsonProperty("quantidade") val quantidade: String?,
    @JsonProperty("tipo_valor_id") val tipoValorId: String?,
    @JsonProperty("nome_tipo_valor") val nomeTipoValor: String?,
    @JsonProperty("valor_custo") val valorCusto: String?,
    @JsonProperty("valor_venda") val valorVenda: String?,
    @JsonProperty("tipo_desconto") val tipoDesconto: String?,
    @JsonProperty("desconto_valor") val descontoValor: String?,
    @JsonProperty("desconto_porcentagem") val descontoPorcentagem: String?,
    @JsonProperty("valor_total") val valorTotal: String?
)

data class SaleServiceWrapperErpResponse(
    @JsonProperty("servico") val servico: SaleServiceErpResponse?
)

data class SaleServiceErpResponse(
    @JsonProperty("id") val id: String?,
    @JsonProperty("servico_id") val servicoId: String?,
    @JsonProperty("nome_servico") val nomeServico: String?,
    @JsonProperty("detalhes") val detalhes: String?,
    @JsonProperty("sigla_unidade") val siglaUnidade: String?,
    @JsonProperty("quantidade") val quantidade: String?,
    @JsonProperty("tipo_valor_id") val tipoValorId: String?,
    @JsonProperty("nome_tipo_valor") val nomeTipoValor: String?,
    @JsonProperty("valor_custo") val valorCusto: String?,
    @JsonProperty("valor_venda") val valorVenda: String?,
    @JsonProperty("tipo_desconto") val tipoDesconto: String?,
    @JsonProperty("desconto_valor") val descontoValor: String?,
    @JsonProperty("desconto_porcentagem") val descontoPorcentagem: String?,
    @JsonProperty("valor_total") val valorTotal: String?
)
