package com.gestaoclick.mcp.application.query

import com.gestaoclick.mcp.integration.erp.*
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetProductsReportHandlerTest {

    @MockK
    lateinit var erpClient: ErpClient

    @InjectMockKs
    lateinit var handler: GetProductsReportHandler

    @Test
    fun `should return report with summary for single page`() {
        val products = listOf(
            buildProductErp(id = "1", estoque = 10, valorCusto = "80.00", valorVenda = "150.00", ativo = "1"),
            buildProductErp(id = "2", estoque = 5, valorCusto = "40.00", valorVenda = "90.00", ativo = "0")
        )
        every { erpClient.get("produtos", ProductsListErpResponse::class.java, any()) } returns
            ProductsListErpResponse(200, "success", buildMeta(), products)

        val result = handler.handle(GetProductsReportQuery())

        assertThat(result.total).isEqualTo(2)
        assertThat(result.summary.totalProducts).isEqualTo(2)
        assertThat(result.summary.totalActiveProducts).isEqualTo(1)
        assertThat(result.summary.totalInactiveProducts).isEqualTo(1)
        assertThat(result.summary.totalStock).isEqualTo(15)
        assertThat(result.summary.totalCostValue).isEqualTo("120.00")
        assertThat(result.summary.totalSaleValue).isEqualTo("240.00")
    }

    @Test
    fun `should aggregate products across multiple pages`() {
        val page1 = ProductsListErpResponse(
            200, "success", buildMeta(proximaPagina = 2), listOf(buildProductErp(id = "1", estoque = 10))
        )
        val page2 = ProductsListErpResponse(
            200, "success", buildMeta(), listOf(buildProductErp(id = "2", estoque = 20))
        )

        every { erpClient.get("produtos", ProductsListErpResponse::class.java, match { it["pagina"] == "1" }) } returns page1
        every { erpClient.get("produtos", ProductsListErpResponse::class.java, match { it["pagina"] == "2" }) } returns page2

        val result = handler.handle(GetProductsReportQuery())

        assertThat(result.total).isEqualTo(2)
        assertThat(result.products.map { it.id }).containsExactly("1", "2")
        assertThat(result.summary.totalStock).isEqualTo(30)
    }

    @Test
    fun `should include filters in response`() {
        every { erpClient.get("produtos", ProductsListErpResponse::class.java, any()) } returns
            ProductsListErpResponse(200, "success", buildMeta(), emptyList())

        val result = handler.handle(GetProductsReportQuery(nome = "TV", ativo = "1"))

        assertThat(result.filters.nome).isEqualTo("TV")
        assertThat(result.filters.ativo).isEqualTo("1")
        assertThat(result.filters.grupoId).isNull()
    }

    @Test
    fun `should handle empty product list`() {
        every { erpClient.get("produtos", ProductsListErpResponse::class.java, any()) } returns
            ProductsListErpResponse(200, "success", buildMeta(), emptyList())

        val result = handler.handle(GetProductsReportQuery())

        assertThat(result.total).isEqualTo(0)
        assertThat(result.summary.totalProducts).isEqualTo(0)
        assertThat(result.summary.totalStock).isEqualTo(0)
        assertThat(result.summary.totalCostValue).isEqualTo("0")
        assertThat(result.summary.totalSaleValue).isEqualTo("0")
    }

    @Test
    fun `should handle null cost and sale values in summary`() {
        val products = listOf(
            buildProductErp(id = "1", valorCusto = null, valorVenda = null),
            buildProductErp(id = "2", valorCusto = "100.00", valorVenda = "200.00")
        )
        every { erpClient.get("produtos", ProductsListErpResponse::class.java, any()) } returns
            ProductsListErpResponse(200, "success", buildMeta(), products)

        val result = handler.handle(GetProductsReportQuery())

        assertThat(result.summary.totalCostValue).isEqualTo("100.00")
        assertThat(result.summary.totalSaleValue).isEqualTo("200.00")
    }

    private fun buildMeta(proximaPagina: Int? = null) = ProductsListErpMeta(
        totalRegistros = 1, totalRegistrosPagina = 1, paginaAtual = 1,
        limitePorPagina = 20, paginaAnterior = null, urlAnterior = null,
        proximaPagina = proximaPagina, proximaUrl = null
    )

    private fun buildProductErp(
        id: String = "1",
        estoque: Int = 0,
        valorCusto: String? = "50.00",
        valorVenda: String? = "75.00",
        ativo: String = "1"
    ) = ProductErpResponse(
        id = id, nome = "Product $id", codigoInterno = "001", codigoBarra = "123456",
        possuiVariacao = "0", possuiComposicao = "0", movimentaEstoque = "1",
        peso = "0.000", largura = "0.000", altura = "0.000", comprimento = "0.000",
        ativo = ativo, grupoId = "100", nomeGrupo = "Geral", descricao = "",
        estoque = estoque, valorCusto = valorCusto, valorVenda = valorVenda,
        valores = null, variacoes = null, fiscal = null
    )
}
