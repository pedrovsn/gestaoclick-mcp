package com.gestaoclick.mcp.application.query

import com.gestaoclick.mcp.integration.erp.*
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class ListSalesByPeriodHandlerTest {

    @MockK
    lateinit var erpClient: ErpClient

    @InjectMockKs
    lateinit var handler: ListSalesByPeriodHandler

    @Test
    fun `should return sales for single page response`() {
        val sale = buildSaleErp(id = "1", nomeCliente = "Cliente A", valorTotal = "100.00")
        every { erpClient.get("vendas", SalesListErpResponse::class.java, any()) } returns
            SalesListErpResponse(200, "success", buildSalesMeta(), listOf(sale))

        val query = ListSalesByPeriodQuery(
            dataInicio = LocalDate.of(2024, 1, 1),
            dataFim = LocalDate.of(2024, 1, 31)
        )
        val result = handler.handle(query)

        assertThat(result.total).isEqualTo(1)
        assertThat(result.sales).hasSize(1)
        assertThat(result.sales[0].id).isEqualTo("1")
        assertThat(result.sales[0].nomeCliente).isEqualTo("Cliente A")
        assertThat(result.sales[0].valorTotal).isEqualTo("100.00")
        assertThat(result.period.dataInicio).isEqualTo("2024-01-01")
        assertThat(result.period.dataFim).isEqualTo("2024-01-31")
    }

    @Test
    fun `should aggregate sales across multiple pages`() {
        val page1 = SalesListErpResponse(
            200, "success", buildSalesMeta(proximaPagina = 2), listOf(buildSaleErp(id = "1"))
        )
        val page2 = SalesListErpResponse(
            200, "success", buildSalesMeta(), listOf(buildSaleErp(id = "2"))
        )

        every { erpClient.get("vendas", SalesListErpResponse::class.java, match { it["pagina"] == "1" }) } returns page1
        every { erpClient.get("vendas", SalesListErpResponse::class.java, match { it["pagina"] == "2" }) } returns page2

        val result = handler.handle(buildDefaultQuery())

        assertThat(result.total).isEqualTo(2)
        assertThat(result.sales.map { it.id }).containsExactly("1", "2")
    }

    @Test
    fun `should throw when dataInicio is after dataFim`() {
        val query = ListSalesByPeriodQuery(
            dataInicio = LocalDate.of(2024, 2, 1),
            dataFim = LocalDate.of(2024, 1, 1)
        )

        assertThrows<IllegalArgumentException> {
            handler.handle(query)
        }
    }

    @Test
    fun `should map payments correctly`() {
        val sale = buildSaleErp(
            id = "1",
            pagamentos = listOf(
                SalePaymentWrapperErpResponse(
                    SalePaymentErpResponse(
                        dataVencimento = "2024-01-15", valor = "50.00",
                        formaPagamentoId = "1", nomeFormaPagamento = "Pix",
                        planoContasId = "10", nomePlanoConta = "Receita",
                        observacao = "Parcela 1"
                    )
                )
            )
        )
        every { erpClient.get("vendas", SalesListErpResponse::class.java, any()) } returns
            SalesListErpResponse(200, "success", buildSalesMeta(), listOf(sale))

        val result = handler.handle(buildDefaultQuery())

        assertThat(result.sales[0].pagamentos).hasSize(1)
        assertThat(result.sales[0].pagamentos[0].valor).isEqualTo("50.00")
        assertThat(result.sales[0].pagamentos[0].nomeFormaPagamento).isEqualTo("Pix")
    }

    @Test
    fun `should map products and services correctly`() {
        val sale = buildSaleErp(
            id = "1",
            produtos = listOf(
                SaleProductWrapperErpResponse(
                    SaleProductErpResponse(
                        produtoId = 10, variacaoId = 20, nomeProduto = "Widget",
                        detalhes = null, movimentaEstoque = "1", possuiVariacao = "0",
                        siglaUnidade = null, quantidade = "3.00", tipoValorId = null,
                        nomeTipoValor = null, valorCusto = "10.00", valorVenda = "15.00",
                        tipoDesconto = null, descontoValor = null, descontoPorcentagem = null,
                        valorTotal = "45.00"
                    )
                )
            ),
            servicos = listOf(
                SaleServiceWrapperErpResponse(
                    SaleServiceErpResponse(
                        id = "5", servicoId = "50", nomeServico = "Instalacao",
                        detalhes = null, siglaUnidade = null, quantidade = "1.00",
                        tipoValorId = null, nomeTipoValor = null, valorCusto = "0.00",
                        valorVenda = "100.00", tipoDesconto = null, descontoValor = null,
                        descontoPorcentagem = null, valorTotal = "100.00"
                    )
                )
            )
        )
        every { erpClient.get("vendas", SalesListErpResponse::class.java, any()) } returns
            SalesListErpResponse(200, "success", buildSalesMeta(), listOf(sale))

        val result = handler.handle(buildDefaultQuery())

        assertThat(result.sales[0].produtos).hasSize(1)
        assertThat(result.sales[0].produtos[0].nomeProduto).isEqualTo("Widget")
        assertThat(result.sales[0].produtos[0].valorTotal).isEqualTo("45.00")

        assertThat(result.sales[0].servicos).hasSize(1)
        assertThat(result.sales[0].servicos[0].nomeServico).isEqualTo("Instalacao")
        assertThat(result.sales[0].servicos[0].valorTotal).isEqualTo("100.00")
    }

    @Test
    fun `should include filters in response`() {
        every { erpClient.get("vendas", SalesListErpResponse::class.java, any()) } returns
            SalesListErpResponse(200, "success", buildSalesMeta(), emptyList())

        val query = ListSalesByPeriodQuery(
            dataInicio = LocalDate.of(2024, 1, 1),
            dataFim = LocalDate.of(2024, 1, 31),
            lojaId = 5,
            nome = "Test"
        )
        val result = handler.handle(query)

        assertThat(result.filters.lojaId).isEqualTo(5)
        assertThat(result.filters.nome).isEqualTo("Test")
        assertThat(result.filters.situacaoId).isNull()
    }

    private fun buildDefaultQuery() = ListSalesByPeriodQuery(
        dataInicio = LocalDate.of(2024, 1, 1),
        dataFim = LocalDate.of(2024, 1, 31)
    )

    private fun buildSalesMeta(proximaPagina: Int? = null) = SalesListErpMeta(
        totalRegistros = 1, totalDaPagina = 1, paginaAtual = 1,
        limitePorPagina = 20, paginaAnterior = null, urlAnterior = null,
        proximaPagina = proximaPagina, proximaUrl = null
    )

    private fun buildSaleErp(
        id: String = "1",
        nomeCliente: String? = null,
        valorTotal: String? = null,
        pagamentos: List<SalePaymentWrapperErpResponse>? = null,
        produtos: List<SaleProductWrapperErpResponse>? = null,
        servicos: List<SaleServiceWrapperErpResponse>? = null
    ) = SaleErpResponse(
        id = id, codigo = "100", clienteId = "1", nomeCliente = nomeCliente,
        vendedorId = null, nomeVendedor = null, tecnicoId = null, nomeTecnico = null,
        data = "2024-01-15", previsaoEntrega = null, situacaoId = null, nomeSituacao = null,
        valorTotal = valorTotal, transportadoraId = null, nomeTransportadora = null,
        centroCustoId = null, nomeCentroCusto = null, aosCuidadosDe = null, validade = null,
        introducao = null, observacoes = null, observacoesInterna = null, valorFrete = null,
        nomeCanalVenda = null, nomeLoja = null, valorCusto = null, condicaoPagamento = null,
        situacaoFinanceiro = null, situacaoEstoque = null, formaPagamentoId = null,
        dataPrimeiraParcela = null, numeroParcelas = null, intervaloDias = null, hash = null,
        equipamentos = null, pagamentos = pagamentos, produtos = produtos, servicos = servicos
    )
}
