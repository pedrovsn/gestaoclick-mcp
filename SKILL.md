---
name: gestao-click
description: Query sales and products data from the GestaoClick ERP (list sales by period, list products, products report with summary)
---

# GestaoClick ERP API

Use this skill to retrieve sales and products data from the GestaoClick ERP via the internal backend service.

Base URL: `http://gestaoclick-mcp.railway.internal:8080`

## List Sales by Period

To fetch sales, make an HTTP GET request using the `fetch` tool:

```
GET http://gestaoclick-mcp.railway.internal/api/sales?dataInicio=YYYY-MM-DD&dataFim=YYYY-MM-DD
```

### Required Parameters

| Parameter   | Type   | Format     | Description |
|-------------|--------|------------|-------------|
| dataInicio  | string | YYYY-MM-DD | Start date  |
| dataFim     | string | YYYY-MM-DD | End date    |

### Optional Parameters

| Parameter     | Type    | Description                       |
|---------------|---------|-----------------------------------|
| lojaId        | integer | Filter by store ID                |
| codigo        | integer | Filter by sale code               |
| nome          | string  | Filter by customer name (partial) |
| situacaoId    | integer | Filter by sale status ID          |
| clienteId     | integer | Filter by customer ID             |
| centroCustoId | integer | Filter by cost center ID          |
| tipo          | string  | Sale type: `produto`, `servico`, or `vendas_balcao`. Defaults to `produto` |

### Example

Fetch January 2025 sales:

```
GET http://gestaoclick-mcp.railway.internal/api/sales?dataInicio=2025-01-01&dataFim=2025-01-31
```

### Response

```json
{
  "period": { "dataInicio": "2025-01-01", "dataFim": "2025-01-31" },
  "filters": {},
  "total": 1,
  "sales": [
    {
      "id": "12345",
      "codigo": "1001",
      "clienteId": "42",
      "nomeCliente": "Empresa ABC",
      "nomeVendedor": "João Silva",
      "data": "2025-01-10",
      "nomeSituacao": "Concluída",
      "valorTotal": "1500.00",
      "nomeLoja": "Loja Centro",
      "pagamentos": [
        {
          "dataVencimento": "2025-01-10",
          "valor": "1500.00",
          "nomeFormaPagamento": "Cartão de Crédito",
          "nomePlanoConta": "Receitas de Vendas",
          "observacao": null
        }
      ],
      "produtos": [
        {
          "produtoId": 7,
          "nomeProduto": "Produto X",
          "quantidade": "2",
          "valorVenda": "500.00",
          "valorTotal": "1000.00"
        }
      ],
      "servicos": []
    }
  ]
}
```

### Key Fields

- **total**: number of sales returned
- **sales[].nomeCliente**: customer name
- **sales[].nomeVendedor**: salesperson name
- **sales[].nomeSituacao**: sale status (e.g. "Concluída", "Cancelada")
- **sales[].valorTotal**: total value (string, as provided by ERP)
- **sales[].pagamentos**: payment entries with due date, amount, and payment method
- **sales[].produtos**: products with quantity and price
- **sales[].servicos**: services with quantity and price

### Notes

- All monetary values are strings.
- All fields in sales may be null.
- Prefer narrow date ranges to avoid large responses.
- The filters object only shows parameters that were actually applied.
- The `tipo` parameter defaults to `produto`. For accurate sales reports, you must also fetch `vendas_balcao` and combine the results, as counter sales are not included in the default response.

---

## List Products

To fetch products, make an HTTP GET request using the `fetch` tool:

```
GET http://gestaoclick-mcp.railway.internal/api/products
```

### Optional Parameters

| Parameter | Type   | Description                          |
|-----------|--------|--------------------------------------|
| nome      | string | Filter by product name (partial)     |
| ativo     | string | Filter by active status              |
| grupoId   | string | Filter by product group ID           |

### Example

Fetch all active products:

```
GET http://gestaoclick-mcp.railway.internal/api/products?ativo=1
```

### Response

```json
{
  "filters": {},
  "total": 1,
  "products": [
    {
      "id": "123",
      "nome": "Produto X",
      "codigoInterno": "PX-001",
      "codigoBarra": "7891234567890",
      "ativo": "1",
      "grupoId": "5",
      "nomeGrupo": "Eletrônicos",
      "estoque": 50,
      "valorCusto": "80.00",
      "valorVenda": "150.00",
      "valores": [
        {
          "nomeTipo": "Varejo",
          "valorCusto": "80.00",
          "valorVenda": "150.00"
        }
      ],
      "variacoes": [
        {
          "id": "456",
          "nome": "Cor: Azul",
          "estoque": "20",
          "valores": [
            {
              "nomeTipo": "Varejo",
              "valorCusto": "80.00",
              "valorVenda": "150.00"
            }
          ]
        }
      ]
    }
  ]
}
```

### Key Fields

- **total**: number of products returned
- **products[].nome**: product name
- **products[].ativo**: active status
- **products[].nomeGrupo**: product group name
- **products[].estoque**: stock quantity (integer)
- **products[].valorCusto**: cost value (string)
- **products[].valorVenda**: sale value (string)
- **products[].valores**: price entries by type (e.g. retail, wholesale)
- **products[].variacoes**: product variations with their own stock and prices

### Notes

- All monetary values are strings.
- All fields in products may be null.
- The filters object only shows parameters that were actually applied.
- No parameters are required — calling with no filters returns all products.

---

## Products Report

To get a full products report with summary data, make an HTTP GET request using the `fetch` tool:

```
GET http://gestaoclick-mcp.railway.internal/api/reports/products
```

This endpoint orchestrates paginated calls to the ERP until all products are fetched, then returns them with aggregated summary metrics.

### Optional Parameters

| Parameter | Type   | Description                          |
|-----------|--------|--------------------------------------|
| nome      | string | Filter by product name (partial)     |
| ativo     | string | Filter by active status              |
| grupoId   | string | Filter by product group ID           |

### Example

Full report of all products:

```
GET http://gestaoclick-mcp.railway.internal/api/reports/products
```

### Response

```json
{
  "filters": {},
  "summary": {
    "totalProducts": 2,
    "totalActiveProducts": 2,
    "totalInactiveProducts": 0,
    "totalStock": 70,
    "totalCostValue": "160.00",
    "totalSaleValue": "300.00"
  },
  "total": 2,
  "products": [
    {
      "id": "123",
      "nome": "Produto X",
      "codigoInterno": "PX-001",
      "codigoBarra": "7891234567890",
      "ativo": "1",
      "grupoId": "5",
      "nomeGrupo": "Eletrônicos",
      "estoque": 50,
      "valorCusto": "80.00",
      "valorVenda": "150.00",
      "valores": [],
      "variacoes": []
    }
  ]
}
```

### Key Fields

- **summary.totalProducts**: total number of products
- **summary.totalActiveProducts**: count of active products (`ativo = "1"`)
- **summary.totalInactiveProducts**: count of inactive products
- **summary.totalStock**: sum of stock across all products
- **summary.totalCostValue**: sum of cost values (string)
- **summary.totalSaleValue**: sum of sale values (string)
- **total**: number of products returned
- **products[]**: full product list (same structure as List Products endpoint)

### Notes

- This endpoint fetches ALL pages from the ERP — response may be large.
- All monetary values are strings.
- The filters object only shows parameters that were actually applied.
- Use filters to narrow down the report scope if needed.
