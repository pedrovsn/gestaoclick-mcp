package com.gestaoclick.mcp.controller

import com.gestaoclick.mcp.application.query.ListProductsHandler
import com.gestaoclick.mcp.application.query.ListProductsQuery
import com.gestaoclick.mcp.application.query.ProductsListResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/products")
class ProductsController(private val handler: ListProductsHandler) {

    @GetMapping
    fun list(
        @RequestParam(required = false) nome: String?,
        @RequestParam(required = false) ativo: String?,
        @RequestParam(required = false) grupoId: String?
    ): ResponseEntity<ProductsListResponse> {
        val query = ListProductsQuery(nome = nome, ativo = ativo, grupoId = grupoId)
        return ResponseEntity.ok(handler.handle(query))
    }
}
