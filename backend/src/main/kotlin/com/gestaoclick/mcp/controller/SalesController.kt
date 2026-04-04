package com.gestaoclick.mcp.controller

import com.gestaoclick.mcp.application.query.ListSalesByPeriodQuery
import com.gestaoclick.mcp.application.query.ListSalesByPeriodHandler
import com.gestaoclick.mcp.application.query.SalesResponse
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/sales")
class SalesController(private val handler: ListSalesByPeriodHandler) {

    @GetMapping
    fun listByPeriod(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dataInicio: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dataFim: LocalDate,
        @RequestParam(required = false) lojaId: Int?,
        @RequestParam(required = false) codigo: Int?,
        @RequestParam(required = false) nome: String?,
        @RequestParam(required = false) situacaoId: Int?,
        @RequestParam(required = false) clienteId: Int?,
        @RequestParam(required = false) centroCustoId: Int?
    ): ResponseEntity<SalesResponse> {
        val query = ListSalesByPeriodQuery(
            dataInicio = dataInicio,
            dataFim = dataFim,
            lojaId = lojaId,
            codigo = codigo,
            nome = nome,
            situacaoId = situacaoId,
            clienteId = clienteId,
            centroCustoId = centroCustoId
        )
        return ResponseEntity.ok(handler.handle(query))
    }
}
