package com.gestaoclick.mcp.application.query

import com.gestaoclick.mcp.handler.Query
import java.time.LocalDate

data class ListSalesByPeriodQuery(
    val dataInicio: LocalDate,
    val dataFim: LocalDate,
    val lojaId: Int? = null,
    val codigo: Int? = null,
    val nome: String? = null,
    val situacaoId: Int? = null,
    val clienteId: Int? = null,
    val centroCustoId: Int? = null
) : Query
