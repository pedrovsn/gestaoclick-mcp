# TASK-006 - List products from Gestão Click

## Task ID
`TASK-006`

## Title
List products from Gestão Click ERP

## Goal
The service must query the Gestão Click products endpoint and return all products in a normalized format.

This feature allows OpenClaw to consume product catalog data (including pricing tiers and variations) through a clean integration layer on top of the ERP API.

## Context
The ERP API exposes a products endpoint at `/produtos`. Our service should consume that endpoint, support optional filters, and expose an internal endpoint with a clean contract.

The ERP remains the source of truth. We do not persist products locally.

Products can have:
- Multiple price tiers (`valores`) — e.g. "Varejo", "Atacado"
- Multiple variations (`variacoes`) — e.g. colors/sizes, each with its own stock and price tiers
- Fiscal data (`fiscal`) — tax-related fields like NCM, CEST, weights

## Requirements
- Implement a query flow to retrieve products from Gestão Click.
- Handle ERP pagination and return all records.
- Expose a backend endpoint for this query.
- Reuse the existing `ErpClient` for ERP communication.
- Map the ERP response into internal DTOs instead of exposing the raw payload directly.
- Include product price tiers, variations, and variation-level pricing in the response.
- Exclude fiscal data from the API response (not relevant for OpenClaw consumption).

## Inputs
Optional filters (query params):
- `nome` (`string`) — filter by product name
- `ativo` (`string`) — filter by active status ("0" or "1")
- `grupoId` (`string`) — filter by product group

Authentication/config inputs:
- ERP base URL (from existing `ErpProperties`)
- ERP access token (from existing `ErpProperties`)
- ERP secret access token (from existing `ErpProperties`)

## Outputs
The backend must return a successful response containing:
- applied filters (only non-null)
- total number of products returned
- the normalized list of products with pricing and variations

The response must be:
- clean
- stable
- easy to consume by OpenClaw
- independent from the raw ERP envelope (`code`, `status`, `meta`)

## API / External Dependency

### ERP endpoint
`GET {{baseUrl}}/produtos`

### Required headers
- `Content-Type: application/json`
- `access-token: <token>`
- `secret-access-token: <secret>`

### Pagination
The ERP paginates results. The `meta.proxima_pagina` field indicates the next page number. The handler must loop through all pages until no next page is returned.

## Response Definition

The ERP integration layer should model at least the following structures:

### ERP response envelope

```json
{
  "code": 200,
  "status": "success",
  "meta": {
    "total_registros": 2,
    "total_paginas": 1,
    "total_registros_pagina": 2,
    "pagina_atual": 1,
    "limite_por_pagina": 20,
    "pagina_anterior": null,
    "url_anterior": null,
    "proxima_pagina": null,
    "proxima_url": null
  },
  "data": [
    {
      "id": "320",
      "nome": "Blusão Masc Moletom",
      "codigo_interno": "0222",
      "codigo_barra": "2031754031703",
      "possui_variacao": "1",
      "possui_composicao": "0",
      "movimenta_estoque": "1",
      "peso": "0.000",
      "largura": "0.000",
      "altura": "0.000",
      "comprimento": "0.000",
      "ativo": "1",
      "grupo_id": "803218",
      "nome_grupo": "Eletrônicos",
      "descricao": "",
      "estoque": 60,
      "valor_custo": "80.0000",
      "valor_venda": "120.0000",
      "valores": [
        {
          "tipo_id": "90864",
          "nome_tipo": "Varejo",
          "lucro_utilizado": "15.00",
          "valor_custo": "80.0000",
          "valor_venda": "92.0000"
        },
        {
          "tipo_id": "90872",
          "nome_tipo": "Atacado",
          "lucro_utilizado": "30.00",
          "valor_custo": "80.0000",
          "valor_venda": "104.0000"
        }
      ],
      "variacoes": [
        {
          "variacao": {
            "id": "478",
            "nome": "Creme",
            "estoque": "10.00",
            "valores": [
              {
                "tipo_id": "90864",
                "nome_tipo": "Varejo",
                "lucro_utilizado": "41.18",
                "valor_custo": "85.0000",
                "valor_venda": "120.0000"
              },
              {
                "tipo_id": "90872",
                "nome_tipo": "Atacado",
                "lucro_utilizado": "41.18",
                "valor_custo": "85.0000",
                "valor_venda": "120.0000"
              }
            ]
          }
        },
        {
          "variacao": {
            "id": "480",
            "nome": "Marrom",
            "estoque": "20.00",
            "valores": [
              {
                "tipo_id": "90864",
                "nome_tipo": "Varejo",
                "lucro_utilizado": "15.00",
                "valor_custo": "80.0000",
                "valor_venda": "92.0000"
              },
              {
                "tipo_id": "90872",
                "nome_tipo": "Atacado",
                "lucro_utilizado": "30.00",
                "valor_custo": "80.0000",
                "valor_venda": "104.0000"
              }
            ]
          }
        },
        {
          "variacao": {
            "id": "482",
            "nome": "Azul Escuro",
            "estoque": "30.00",
            "valores": [
              {
                "tipo_id": "90864",
                "nome_tipo": "Varejo",
                "lucro_utilizado": "15.00",
                "valor_custo": "80.0000",
                "valor_venda": "92.0000"
              },
              {
                "tipo_id": "90872",
                "nome_tipo": "Atacado",
                "lucro_utilizado": "30.00",
                "valor_custo": "80.0000",
                "valor_venda": "104.0000"
              }
            ]
          }
        }
      ],
      "fiscal": {
        "ncm": "",
        "cest": "",
        "peso_liquido": null,
        "peso_bruto": null,
        "valor_aproximado_tributos": null,
        "valor_fixo_pis": null,
        "valor_fixo_pis_st": null,
        "valor_fixo_confins": null,
        "valor_fixo_confins_st": null
      }
    },
    {
      "id": "319",
      "nome": "Smart TV 4K LED 50",
      "codigo_interno": "011111",
      "codigo_barra": "2086871760609",
      "possui_variacao": "0",
      "possui_composicao": "0",
      "movimenta_estoque": "1",
      "peso": "0.000",
      "largura": "0.000",
      "altura": "0.000",
      "comprimento": "0.000",
      "ativo": "1",
      "grupo_id": "803218",
      "nome_grupo": "Eletrônicos",
      "descricao": "",
      "estoque": 10,
      "valor_custo": "1500.2000",
      "valor_venda": "1725.2300",
      "valores": [
        {
          "tipo_id": "90864",
          "nome_tipo": "Varejo",
          "lucro_utilizado": "15.00",
          "valor_custo": "1500.2000",
          "valor_venda": "1725.2300"
        },
        {
          "tipo_id": "90872",
          "nome_tipo": "Atacado",
          "lucro_utilizado": "30.00",
          "valor_custo": "1500.2000",
          "valor_venda": "1950.2600"
        }
      ],
      "variacoes": [
        {
          "variacao": {
            "id": "476",
            "nome": "",
            "estoque": "10.00",
            "valores": [
              {
                "tipo_id": "90864",
                "nome_tipo": "Varejo",
                "lucro_utilizado": "15.00",
                "valor_custo": "1500.2000",
                "valor_venda": "1725.2300"
              },
              {
                "tipo_id": "90872",
                "nome_tipo": "Atacado",
                "lucro_utilizado": "30.00",
                "valor_custo": "1500.2000",
                "valor_venda": "1950.2600"
              }
            ]
          }
        }
      ],
      "fiscal": {
        "ncm": "85044060",
        "cest": "",
        "peso_liquido": "20.000",
        "peso_bruto": "20.000",
        "valor_aproximado_tributos": null,
        "valor_fixo_pis": null,
        "valor_fixo_pis_st": null,
        "valor_fixo_confins": null,
        "valor_fixo_confins_st": null
      }
    }
  ]
}
```

### Mapping contract for the ERP client layer

#### ProductsListErpResponse
- `code: Int?`
- `status: String?`
- `meta: ProductsListErpMeta?`
- `data: List<ProductErpResponse>?`

#### ProductsListErpMeta
- `total_registros: Int?`
- `total_registros_pagina: Int?`
- `pagina_atual: Int?`
- `limite_por_pagina: Int?`
- `pagina_anterior: Int?`
- `url_anterior: String?`
- `proxima_pagina: Int?`
- `proxima_url: String?`

#### ProductErpResponse
- `id: String?`
- `nome: String?`
- `codigo_interno: String?`
- `codigo_barra: String?`
- `possui_variacao: String?`
- `possui_composicao: String?`
- `movimenta_estoque: String?`
- `peso: String?`
- `largura: String?`
- `altura: String?`
- `comprimento: String?`
- `ativo: String?`
- `grupo_id: String?`
- `nome_grupo: String?`
- `descricao: String?`
- `estoque: Int?`
- `valor_custo: String?`
- `valor_venda: String?`
- `valores: List<ProductPriceErpResponse>?`
- `variacoes: List<ProductVariationWrapperErpResponse>?`
- `fiscal: ProductFiscalErpResponse?`

#### ProductPriceErpResponse
- `tipo_id: String?`
- `nome_tipo: String?`
- `lucro_utilizado: String?`
- `valor_custo: String?`
- `valor_venda: String?`

#### ProductVariationWrapperErpResponse
- `variacao: ProductVariationErpResponse?`

#### ProductVariationErpResponse
- `id: String?`
- `nome: String?`
- `estoque: String?`
- `valores: List<ProductPriceErpResponse>?`

#### ProductFiscalErpResponse
- `ncm: String?`
- `cest: String?`
- `peso_liquido: String?`
- `peso_bruto: String?`
- `valor_aproximado_tributos: String?`
- `valor_fixo_pis: String?`
- `valor_fixo_pis_st: String?`
- `valor_fixo_confins: String?`
- `valor_fixo_confins_st: String?`

### Implementation note
These ERP response DTOs are meant for the integration layer only.

The backend must still map them to internal response models before returning data from our own API. Fiscal data should be modeled in the ERP layer (for completeness) but excluded from the API response.

## Rules
- Do not use a database.
- Do not expose the ERP response envelope directly.
- Do not place HTTP integration code in controllers.
- Handle pagination until all products are retrieved.
- If the ERP returns an error, translate it into a consistent internal error response.
- Keep the implementation simple and incremental.
- Prefer a query use case/handler for this feature.
- Reuse the existing `ErpClient` — do not create a new HTTP client.

## Acceptance Criteria
- An internal endpoint exists to list products (`GET /api/products`).
- The endpoint supports optional filters: `nome`, `ativo`, `grupoId`.
- The service calls the Gestão Click products endpoint with the correct headers.
- The service retrieves all pages when the ERP paginates the results.
- The response includes product price tiers and variations with their pricing.
- Fiscal data is not included in the API response.
- The response returned by our backend is normalized and does not leak the raw ERP envelope.
- ERP failures are mapped to a consistent error format.
- The implementation keeps ERP integration isolated from controllers.
- No persistence layer is introduced.

## Out of Scope
- Persisting product data
- Caching
- Background synchronization
- Product search/full-text search
- Product CRUD operations (create, update, delete)
- Inventory management
- Fiscal/tax calculations

## Notes

### Suggested internal endpoint
`GET /api/products`

Suggested query params:
- `nome`
- `ativo`
- `grupoId`

### Suggested normalized response shape

```json
{
  "filters": {
    "nome": "Blusão"
  },
  "total": 1,
  "products": [
    {
      "id": "320",
      "nome": "Blusão Masc Moletom",
      "codigoInterno": "0222",
      "codigoBarra": "2031754031703",
      "ativo": "1",
      "grupoId": "803218",
      "nomeGrupo": "Eletrônicos",
      "estoque": 60,
      "valorCusto": "80.0000",
      "valorVenda": "120.0000",
      "valores": [
        {
          "nomeTipo": "Varejo",
          "valorCusto": "80.0000",
          "valorVenda": "92.0000"
        },
        {
          "nomeTipo": "Atacado",
          "valorCusto": "80.0000",
          "valorVenda": "104.0000"
        }
      ],
      "variacoes": [
        {
          "id": "478",
          "nome": "Creme",
          "estoque": "10.00",
          "valores": [
            {
              "nomeTipo": "Varejo",
              "valorCusto": "85.0000",
              "valorVenda": "120.0000"
            },
            {
              "nomeTipo": "Atacado",
              "valorCusto": "85.0000",
              "valorVenda": "120.0000"
            }
          ]
        }
      ]
    }
  ]
}
```

### Mapping guidance
Map the most relevant product fields. Include price tiers and variations since they are essential for product catalog consumption. Exclude fiscal data from the API response — it adds noise and is not needed by OpenClaw.
