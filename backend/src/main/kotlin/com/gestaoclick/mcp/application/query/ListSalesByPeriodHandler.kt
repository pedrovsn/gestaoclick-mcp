package com.gestaoclick.mcp.application.query

import com.gestaoclick.mcp.integration.erp.ErpClient
import com.gestaoclick.mcp.integration.erp.SaleErpResponse
import com.gestaoclick.mcp.integration.erp.SalePaymentErpResponse
import com.gestaoclick.mcp.integration.erp.SaleProductErpResponse
import com.gestaoclick.mcp.integration.erp.SaleServiceErpResponse
import com.gestaoclick.mcp.integration.erp.SalesListErpResponse
import org.springframework.stereotype.Component

@Component
class ListSalesByPeriodHandler(private val erpClient: ErpClient) {

    fun handle(query: ListSalesByPeriodQuery): SalesResponse {
        require(!query.dataInicio.isAfter(query.dataFim)) {
            "dataInicio must not be after dataFim"
        }

        val allSales = mutableListOf<SaleErpResponse>()
        var page = 1

        do {
            val response = erpClient.get("vendas", SalesListErpResponse::class.java, buildParams(query, page))
            allSales.addAll(response.data)
            val nextPage = response.meta.proximaPagina ?: break
            page = nextPage
        } while (true)

        return SalesResponse(
            period = PeriodResponse(
                dataInicio = query.dataInicio.toString(),
                dataFim = query.dataFim.toString()
            ),
            filters = FiltersResponse(
                lojaId = query.lojaId,
                codigo = query.codigo,
                nome = query.nome,
                situacaoId = query.situacaoId,
                clienteId = query.clienteId,
                centroCustoId = query.centroCustoId
            ),
            total = allSales.size,
            sales = allSales.map { it.toSaleResponse() }
        )
    }

    private fun buildParams(query: ListSalesByPeriodQuery, page: Int): Map<String, String?> = mapOf(
        "data_inicio" to query.dataInicio.toString(),
        "data_fim" to query.dataFim.toString(),
        "pagina" to page.toString(),
        "loja_id" to query.lojaId?.toString(),
        "codigo" to query.codigo?.toString(),
        "nome" to query.nome,
        "situacao_id" to query.situacaoId?.toString(),
        "cliente_id" to query.clienteId?.toString(),
        "centro_custo_id" to query.centroCustoId?.toString()
    )
}

private fun SaleErpResponse.toSaleResponse() = SaleResponse(
    id = id,
    codigo = codigo,
    clienteId = clienteId,
    nomeCliente = nomeCliente,
    nomeVendedor = nomeVendedor,
    data = data,
    nomeSituacao = nomeSituacao,
    valorTotal = valorTotal,
    nomeLoja = nomeLoja,
    pagamentos = pagamentos.map { it.pagamento.toPaymentResponse() },
    produtos = produtos.map { it.produto.toProductResponse() },
    servicos = servicos.map { it.servico.toServiceResponse() }
)

private fun SalePaymentErpResponse.toPaymentResponse() = PaymentResponse(
    dataVencimento = dataVencimento,
    valor = valor,
    nomeFormaPagamento = nomeFormaPagamento,
    nomePlanoConta = nomePlanoConta,
    observacao = observacao
)

private fun SaleProductErpResponse.toProductResponse() = ProductResponse(
    produtoId = produtoId,
    nomeProduto = nomeProduto,
    quantidade = quantidade,
    valorVenda = valorVenda,
    valorTotal = valorTotal
)

private fun SaleServiceErpResponse.toServiceResponse() = ServiceResponse(
    servicoId = servicoId,
    nomeServico = nomeServico,
    quantidade = quantidade,
    valorVenda = valorVenda,
    valorTotal = valorTotal
)
