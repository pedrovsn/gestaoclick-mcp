# TASK-003 - List sales by period from Gestão Click

## Task ID
`TASK-003`

## Title
List sales from Gestão Click for a given period

## Goal
Given a date range, the service must query the Gestão Click API and return all sales for that period.

This feature should allow our backend to act as a clean integration layer on top of the ERP API, so OpenClaw can consume sales data in a simpler and more controlled way.

## Context
The ERP API already exposes a sales endpoint. Our service should consume that endpoint, apply the supported filters, and expose an internal endpoint with a clean contract.

The ERP remains the source of truth. We do not persist sales locally.

## Requirements
- Implement a query flow to retrieve sales from Gestão Click.
- Accept at least `data_inicio` and `data_fim` as filters.
- Support forwarding optional ERP filters when relevant.
- Handle ERP pagination and return all records for the requested period.
- Expose a backend endpoint for this query.
- Isolate ERP communication inside a dedicated client/gateway.
- Validate inputs before calling the ERP API.
- Map the ERP response into internal DTOs instead of exposing the raw payload directly.

## Inputs
Required input:
- `dataInicio` in format `YYYY-MM-DD`
- `dataFim` in format `YYYY-MM-DD`

Optional filters:
- `lojaId` (`int`)
- `codigo` (`int`)
- `nome` (`string`)
- `situacaoId` (`int`)
- `clienteId` (`int`)
- `centroCustoId` (`int`)

Authentication/config inputs:
- ERP base URL
- ERP access token
- ERP secret access token

## Outputs
The backend must return a successful response containing:
- the requested period
- applied filters
- total number of sales returned
- the normalized list of sales

The exact response shape may be designed by the implementation, but it must be:
- clean
- stable
- easy to consume by OpenClaw
- independent from the raw ERP envelope (`code`, `status`, `meta`)

## API / External Dependency

### ERP endpoint
`GET {{baseUrl}}/vendas`

### Required headers
- `Content-Type: application/json`
- `access-token: <token>`
- `secret-access-token: <secret>`

### Supported ERP filters
- `loja_id` (`int`)
  - To discover store ids, the ERP suggests `GET /api/lojas/tipo`
  - Supported `tipo` values mentioned in the ERP docs: `produto`, `servico`, `vendas_balcao`
- `codigo` (`int`)
- `nome` (`string`)
- `situacao_id` (`int`)
  - Situation ids can be discovered via `GET /api/situacoes_vendas/`
- `data_inicio` (`YYYY-MM-DD`)
- `data_fim` (`YYYY-MM-DD`)
- `cliente_id` (`int`)
  - Client ids can be discovered via `GET /api/clientes/`
- `centro_custo_id` (`int`)
  - Cost center ids can be discovered via `GET /api/centros_custos/`

### Sample ERP response
The ERP returns an envelope with fields like:
- `code`
- `status`
- `meta`
- `data`

Where `meta` contains pagination information and `data` contains the list of sales.

The sample payload provided for this task must be used as the reference for field mapping.

## Response Definition

The backend implementation must use the ERP response below as the mapping reference for the external client DTOs.

### ERP response envelope

```json
{
  "code": 200,
  "status": "success",
  "meta": {
    "total_registros": 1,
    "total_da_pagina": 1,
    "pagina_atual": 1,
    "limite_por_pagina": 20,
    "pagina_anterior": null,
    "url_anterior": null,
    "proxima_pagina": null,
    "proxima_url": null
  },
  "data": [
    {
      "id": "505",
      "codigo": "795",
      "cliente_id": "1",
      "nome_cliente": "Ronei Marcos Silva Marques",
      "vendedor_id": "45",
      "nome_vendedor": "João da Silva",
      "tecnico_id": null,
      "nome_tecnico": null,
      "data": "2020-01-27",
      "previsao_entrega": null,
      "situacao_id": "3150",
      "nome_situacao": "Confirmado",
      "valor_total": "60.00",
      "transportadora_id": null,
      "nome_transportadora": null,
      "centro_custo_id": "1",
      "nome_centro_custo": "Centro de Custo 01",
      "aos_cuidados_de": null,
      "validade": null,
      "introducao": null,
      "observacoes": null,
      "observacoes_interna": null,
      "valor_frete": "0.00",
      "nome_canal_venda": "Kautrite III",
      "nome_loja": "Savassi",
      "valor_custo": "0.00",
      "condicao_pagamento": "parcelado",
      "situacao_financeiro": "1",
      "situacao_estoque": "1",
      "forma_pagamento_id": "579722",
      "data_primeira_parcela": "2020-01-27",
      "numero_parcelas": "2",
      "intervalo_dias": "30",
      "hash": "wpQseRf",
      "equipamentos": [],
      "pagamentos": [
        {
          "pagamento": {
            "data_vencimento": "2020-01-27",
            "valor": "25.00",
            "forma_pagamento_id": "579722",
            "nome_forma_pagamento": "BB",
            "plano_contas_id": "2514",
            "nome_plano_conta": "Prestações de serviçosAC",
            "observacao": "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
          }
        },
        {
          "pagamento": {
            "data_vencimento": "2020-02-27",
            "valor": "35.00",
            "forma_pagamento_id": "579722",
            "nome_forma_pagamento": "BB",
            "plano_contas_id": "2514",
            "nome_plano_conta": "Prestações de serviçosAC",
            "observacao": "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
          }
        }
      ],
      "produtos": [
        {
          "produto": {
            "produto_id": 1238787,
            "variacao_id": 4152212,
            "nome_produto": null,
            "detalhes": "Lorem Ipsum is simply dummy text of the",
            "movimenta_estoque": "0",
            "possui_variacao": "0",
            "sigla_unidade": null,
            "quantidade": "1.00",
            "tipo_valor_id": null,
            "nome_tipo_valor": null,
            "valor_custo": "0.00",
            "valor_venda": "60.00",
            "tipo_desconto": "R$",
            "desconto_valor": null,
            "desconto_porcentagem": null,
            "valor_total": "60.00"
          }
        }
      ],
      "servicos": [
        {
          "servico": {
            "id": "351",
            "servico_id": "437",
            "nome_servico": "Serviço 01",
            "detalhes": "",
            "sigla_unidade": null,
            "quantidade": "1.00",
            "tipo_valor_id": null,
            "nome_tipo_valor": null,
            "valor_custo": "0.0000",
            "valor_venda": "25.0000",
            "tipo_desconto": "%",
            "desconto_valor": null,
            "desconto_porcentagem": "5.0000",
            "valor_total": "23.75"
          }
        }
      ]
    }
  ]
}
```

### Mapping contract for the ERP client layer

The ERP integration layer should model at least the following structures:

#### SalesListErpResponse
- `code: Int`
- `status: String`
- `meta: SalesListErpMeta`
- `data: List<SaleErpResponse>`

#### SalesListErpMeta
- `total_registros: Int`
- `total_da_pagina: Int`
- `pagina_atual: Int`
- `limite_por_pagina: Int`
- `pagina_anterior: Int?`
- `url_anterior: String?`
- `proxima_pagina: Int?`
- `proxima_url: String?`

#### SaleErpResponse
- `id: String`
- `codigo: String`
- `cliente_id: String?`
- `nome_cliente: String?`
- `vendedor_id: String?`
- `nome_vendedor: String?`
- `tecnico_id: String?`
- `nome_tecnico: String?`
- `data: String`
- `previsao_entrega: String?`
- `situacao_id: String?`
- `nome_situacao: String?`
- `valor_total: String?`
- `transportadora_id: String?`
- `nome_transportadora: String?`
- `centro_custo_id: String?`
- `nome_centro_custo: String?`
- `aos_cuidados_de: String?`
- `validade: String?`
- `introducao: String?`
- `observacoes: String?`
- `observacoes_interna: String?`
- `valor_frete: String?`
- `nome_canal_venda: String?`
- `nome_loja: String?`
- `valor_custo: String?`
- `condicao_pagamento: String?`
- `situacao_financeiro: String?`
- `situacao_estoque: String?`
- `forma_pagamento_id: String?`
- `data_primeira_parcela: String?`
- `numero_parcelas: String?`
- `intervalo_dias: String?`
- `hash: String?`
- `equipamentos: List<Any>`
- `pagamentos: List<SalePaymentWrapperErpResponse>`
- `produtos: List<SaleProductWrapperErpResponse>`
- `servicos: List<SaleServiceWrapperErpResponse>`

#### SalePaymentWrapperErpResponse
- `pagamento: SalePaymentErpResponse`

#### SalePaymentErpResponse
- `data_vencimento: String?`
- `valor: String?`
- `forma_pagamento_id: String?`
- `nome_forma_pagamento: String?`
- `plano_contas_id: String?`
- `nome_plano_conta: String?`
- `observacao: String?`

#### SaleProductWrapperErpResponse
- `produto: SaleProductErpResponse`

#### SaleProductErpResponse
- `produto_id: Long?`
- `variacao_id: Long?`
- `nome_produto: String?`
- `detalhes: String?`
- `movimenta_estoque: String?`
- `possui_variacao: String?`
- `sigla_unidade: String?`
- `quantidade: String?`
- `tipo_valor_id: String?`
- `nome_tipo_valor: String?`
- `valor_custo: String?`
- `valor_venda: String?`
- `tipo_desconto: String?`
- `desconto_valor: String?`
- `desconto_porcentagem: String?`
- `valor_total: String?`

#### SaleServiceWrapperErpResponse
- `servico: SaleServiceErpResponse`

#### SaleServiceErpResponse
- `id: String?`
- `servico_id: String?`
- `nome_servico: String?`
- `detalhes: String?`
- `sigla_unidade: String?`
- `quantidade: String?`
- `tipo_valor_id: String?`
- `nome_tipo_valor: String?`
- `valor_custo: String?`
- `valor_venda: String?`
- `tipo_desconto: String?`
- `desconto_valor: String?`
- `desconto_porcentagem: String?`
- `valor_total: String?`

### Implementation note
These ERP response DTOs are meant for the integration layer only.

The backend must still map them to internal response models before returning data from our own API.

## Rules
- Do not use a database.
- Do not expose the ERP response envelope directly.
- Do not place HTTP integration code in controllers.
- Handle pagination until all sales for the requested period are retrieved.
- If the ERP returns an error, translate it into a consistent internal error response.
- If `dataInicio` is after `dataFim`, return a validation error.
- Keep the implementation simple and incremental.
- Prefer a query use case/handler for this feature.

## Acceptance Criteria
- An internal endpoint exists to list sales by period.
- The endpoint validates `dataInicio` and `dataFim`.
- The endpoint supports the optional filters listed in this task.
- The service calls the Gestão Click sales endpoint with the correct headers.
- The service retrieves all pages when the ERP paginates the results.
- The response returned by our backend is normalized and does not leak the raw ERP envelope.
- ERP failures are mapped to a consistent error format.
- The implementation keeps ERP integration isolated from controllers.
- No persistence layer is introduced.

## Out of Scope
- Persisting sales data
- Caching
- Background synchronization
- Discovering and caching store/client/cost-center/sales-situation ids
- Implementing every possible ERP sales filter not listed in this task
- Generating final business reports beyond listing normalized sales data

## Notes

### Suggested internal endpoint
One possible internal endpoint is:

`GET /api/sales`

Suggested query params:
- `dataInicio`
- `dataFim`
- `lojaId`
- `codigo`
- `nome`
- `situacaoId`
- `clienteId`
- `centroCustoId`

### Suggested normalized response shape
Example:

```json
{
  "period": {
    "dataInicio": "2020-01-01",
    "dataFim": "2020-01-31"
  },
  "filters": {
    "lojaId": 1,
    "situacaoId": 3150
  },
  "total": 1,
  "sales": [
    {
      "id": "505",
      "codigo": "795",
      "clienteId": "1",
      "nomeCliente": "Ronei Marcos Silva Marques",
      "nomeVendedor": "João da Silva",
      "data": "2020-01-27",
      "nomeSituacao": "Confirmado",
      "valorTotal": "60.00",
      "nomeLoja": "Savassi",
      "pagamentos": [],
      "produtos": [],
      "servicos": []
    }
  ]
}
```

### Mapping guidance
Map the most relevant sale fields first. Start simple, but keep the mapping structure ready for nested payment, product, and service items.
