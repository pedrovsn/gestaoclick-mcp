package com.gestaoclick.mcp.controller

import com.gestaoclick.mcp.application.query.GetProductsReportHandler
import com.gestaoclick.mcp.application.query.GetProductsReportQuery
import com.gestaoclick.mcp.application.query.ProductsReportResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/reports/products")
class ProductsReportController(private val handler: GetProductsReportHandler) {

    @GetMapping
    fun getReport(
        @RequestParam(required = false) nome: String?,
        @RequestParam(required = false) ativo: String?,
        @RequestParam(required = false) grupoId: String?
    ): ResponseEntity<ProductsReportResponse> {
        val query = GetProductsReportQuery(nome = nome, ativo = ativo, grupoId = grupoId)
        return ResponseEntity.ok(handler.handle(query))
    }
}
