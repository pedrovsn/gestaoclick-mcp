package com.gestaoclick.mcp.application.query

import com.gestaoclick.mcp.handler.Query

data class GetProductsReportQuery(
    val nome: String? = null,
    val ativo: String? = null,
    val grupoId: String? = null
) : Query
