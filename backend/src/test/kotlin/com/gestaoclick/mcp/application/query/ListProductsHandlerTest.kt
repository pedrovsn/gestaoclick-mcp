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
class ListProductsHandlerTest {

    @MockK
    lateinit var erpClient: ErpClient

    @InjectMockKs
    lateinit var handler: ListProductsHandler

    @Test
    fun `should return products for single page response`() {
        val product = buildProductErp(id = "1", nome = "TV 50", estoque = 10)
        every { erpClient.get("produtos", ProductsListErpResponse::class.java, any()) } returns
            ProductsListErpResponse(200, "success", buildProductsMeta(), listOf(product))

        val result = handler.handle(ListProductsQuery())

        assertThat(result.total).isEqualTo(1)
        assertThat(result.products).hasSize(1)
        assertThat(result.products[0].id).isEqualTo("1")
        assertThat(result.products[0].nome).isEqualTo("TV 50")
        assertThat(result.products[0].estoque).isEqualTo(10)
    }

    @Test
    fun `should aggregate products across multiple pages`() {
        val page1 = ProductsListErpResponse(
            200, "success", buildProductsMeta(proximaPagina = 2), listOf(buildProductErp(id = "1"))
        )
        val page2 = ProductsListErpResponse(
            200, "success", buildProductsMeta(), listOf(buildProductErp(id = "2"))
        )

        every { erpClient.get("produtos", ProductsListErpResponse::class.java, match { it["pagina"] == "1" }) } returns page1
        every { erpClient.get("produtos", ProductsListErpResponse::class.java, match { it["pagina"] == "2" }) } returns page2

        val result = handler.handle(ListProductsQuery())

        assertThat(result.total).isEqualTo(2)
        assertThat(result.products.map { it.id }).containsExactly("1", "2")
    }

    @Test
    fun `should map price tiers correctly`() {
        val product = buildProductErp(
            id = "1",
            valores = listOf(
                ProductPriceErpResponse("90864", "Varejo", "15.00", "80.00", "92.00"),
                ProductPriceErpResponse("90872", "Atacado", "30.00", "80.00", "104.00")
            )
        )
        every { erpClient.get("produtos", ProductsListErpResponse::class.java, any()) } returns
            ProductsListErpResponse(200, "success", buildProductsMeta(), listOf(product))

        val result = handler.handle(ListProductsQuery())

        val valores = result.products[0].valores
        assertThat(valores).hasSize(2)
        assertThat(valores[0].nomeTipo).isEqualTo("Varejo")
        assertThat(valores[0].valorCusto).isEqualTo("80.00")
        assertThat(valores[0].valorVenda).isEqualTo("92.00")
        assertThat(valores[1].nomeTipo).isEqualTo("Atacado")
    }

    @Test
    fun `should map variations with nested pricing`() {
        val product = buildProductErp(
            id = "1",
            variacoes = listOf(
                ProductVariationWrapperErpResponse(
                    ProductVariationErpResponse(
                        id = "478", nome = "Creme", estoque = "10.00",
                        valores = listOf(
                            ProductPriceErpResponse("90864", "Varejo", "41.18", "85.00", "120.00")
                        )
                    )
                ),
                ProductVariationWrapperErpResponse(
                    ProductVariationErpResponse(
                        id = "480", nome = "Marrom", estoque = "20.00",
                        valores = listOf(
                            ProductPriceErpResponse("90864", "Varejo", "15.00", "80.00", "92.00")
                        )
                    )
                )
            )
        )
        every { erpClient.get("produtos", ProductsListErpResponse::class.java, any()) } returns
            ProductsListErpResponse(200, "success", buildProductsMeta(), listOf(product))

        val result = handler.handle(ListProductsQuery())

        val variacoes = result.products[0].variacoes
        assertThat(variacoes).hasSize(2)
        assertThat(variacoes[0].id).isEqualTo("478")
        assertThat(variacoes[0].nome).isEqualTo("Creme")
        assertThat(variacoes[0].estoque).isEqualTo("10.00")
        assertThat(variacoes[0].valores).hasSize(1)
        assertThat(variacoes[0].valores[0].nomeTipo).isEqualTo("Varejo")
        assertThat(variacoes[0].valores[0].valorVenda).isEqualTo("120.00")
        assertThat(variacoes[1].nome).isEqualTo("Marrom")
    }

    @Test
    fun `should handle null variations wrapper gracefully`() {
        val product = buildProductErp(
            id = "1",
            variacoes = listOf(ProductVariationWrapperErpResponse(variacao = null))
        )
        every { erpClient.get("produtos", ProductsListErpResponse::class.java, any()) } returns
            ProductsListErpResponse(200, "success", buildProductsMeta(), listOf(product))

        val result = handler.handle(ListProductsQuery())

        assertThat(result.products[0].variacoes).isEmpty()
    }

    @Test
    fun `should include filters in response`() {
        every { erpClient.get("produtos", ProductsListErpResponse::class.java, any()) } returns
            ProductsListErpResponse(200, "success", buildProductsMeta(), emptyList())

        val query = ListProductsQuery(nome = "TV", ativo = "1")
        val result = handler.handle(query)

        assertThat(result.filters.nome).isEqualTo("TV")
        assertThat(result.filters.ativo).isEqualTo("1")
        assertThat(result.filters.grupoId).isNull()
    }

    @Test
    fun `should handle empty product list`() {
        every { erpClient.get("produtos", ProductsListErpResponse::class.java, any()) } returns
            ProductsListErpResponse(200, "success", buildProductsMeta(), emptyList())

        val result = handler.handle(ListProductsQuery())

        assertThat(result.total).isEqualTo(0)
        assertThat(result.products).isEmpty()
    }

    private fun buildProductsMeta(proximaPagina: Int? = null) = ProductsListErpMeta(
        totalRegistros = 1, totalRegistrosPagina = 1, paginaAtual = 1,
        limitePorPagina = 20, paginaAnterior = null, urlAnterior = null,
        proximaPagina = proximaPagina, proximaUrl = null
    )

    private fun buildProductErp(
        id: String = "1",
        nome: String = "Product",
        estoque: Int = 0,
        valores: List<ProductPriceErpResponse>? = null,
        variacoes: List<ProductVariationWrapperErpResponse>? = null
    ) = ProductErpResponse(
        id = id, nome = nome, codigoInterno = "001", codigoBarra = "123456",
        possuiVariacao = "0", possuiComposicao = "0", movimentaEstoque = "1",
        peso = "0.000", largura = "0.000", altura = "0.000", comprimento = "0.000",
        ativo = "1", grupoId = "100", nomeGrupo = "Geral", descricao = "",
        estoque = estoque, valorCusto = "50.00", valorVenda = "75.00",
        valores = valores, variacoes = variacoes, fiscal = null
    )
}
