# backend/AGENTS.md

## Escopo

A pasta `backend` contém a API em Kotlin + Spring Boot responsável por:

- consumir a API do ERP
- transformar os dados retornados
- expor endpoints internos para relatórios e consultas personalizadas
- servir de base para integração com OpenClaw/MCP

## Regras obrigatórias

### 1. Stack e linguagem
- Usar Kotlin idiomático.
- Usar Spring Boot.
- Preferir recursos nativos do Spring antes de adicionar bibliotecas extras.
- Manter o número de dependências o menor possível.

### 2. Sem persistência
- Não criar entidades JPA.
- Não adicionar Spring Data JPA.
- Não criar repositórios de banco.
- Não introduzir migrations.
- Não depender de banco para funcionar.

### 3. CQRS simples
Separar responsabilidades em:
- `command`: operações que iniciam alguma ação/orquestração
- `query`: operações de leitura

Essa separação é organizacional, não distribuída.

#### O que isso significa na prática
- Queries retornam dados e não alteram estado de domínio.
- Commands podem disparar fluxos como sincronização sob demanda, composição, validação ou preparação de relatório.
- Não criar barramento de comandos ou queries complexo se chamadas diretas resolverem.
- Interfaces base como `CommandHandler` e `QueryHandler` são bem-vindas se permanecerem simples.

### 4. Estrutura sugerida
A estrutura deve continuar enxuta. Uma sugestão:

```text
backend/src/main/kotlin/.../
  api/
  application/
    command/
    query/
  integration/
    erp/
  shared/
    exception/
    config/
```

Se necessário, também pode haver:
- `mcp/`
- `dto/`

Evitar criar muitas camadas artificiais.

### 5. Integração com ERP
Toda chamada ao ERP deve:
- passar por client/gateway dedicado
- ter timeout configurado
- tratar falhas de forma explícita
- mapear respostas externas para modelos internos quando isso melhorar o contrato

#### Regras de integração
- Não misturar lógica HTTP diretamente em controller.
- Não espalhar chamadas ao ERP por vários pontos da aplicação.
- Centralizar autenticação, headers e base URL em configuração.
- Tratar códigos 4xx/5xx com exceções específicas quando fizer sentido.

### 6. Controllers
- Controllers devem ser finos.
- Controllers apenas recebem requisição, validam entrada e delegam.
- A lógica deve ficar em handlers/services.
- Usar DTOs de request/response claros.
- Não retornar stacktrace ou mensagens internas para o consumidor.

### 7. Tratamento de erros
- Criar tratamento global de exceções.
- Padronizar payload de erro.
- Mensagens devem ser úteis e seguras.
- Diferenciar erro de validação, erro de integração externa e erro interno.

### 8. Validação
- Validar inputs na borda.
- Usar Bean Validation quando ajudar.
- Validar parâmetros de data, paginação e filtros.
- Não confiar no ERP para fazer toda validação por nós.

### 9. Modelagem
- Modelos internos devem refletir o domínio do relatório, não apenas o formato cru do ERP.
- Evitar vazar DTO externo diretamente na API pública.
- Usar `data class` sempre que fizer sentido.
- Preferir nullability explícita a valores mágicos.

### 10. Estilo de código Kotlin
- Preferir imutabilidade.
- Preferir `when`, `let`, `run`, `takeIf` etc. apenas quando aumentarem clareza.
- Evitar encadeamentos excessivos.
- Evitar funções enormes.
- Nomes devem ser descritivos e diretos.
- Evitar sufixos genéricos como `Util`, `Helper`, `Manager` sem necessidade real.

### 11. Testes
Prioridade inicial:
- testes unitários para handlers
- testes para mapeadores críticos
- testes para client/integration quando houver lógica relevante

Não criar suíte de testes excessiva no início.
Focar nos fluxos principais e nos casos de erro mais importantes.

### 12. Observabilidade
Adicionar apenas o básico:
- logs úteis
- healthcheck
- readiness/liveness se necessário

Evitar instrumentação complexa nesta fase inicial.

### 13. Segurança
Se autenticação ainda não estiver definida:
- não inventar solução complexa
- preparar a aplicação para receber auth futuramente
- proteger segredos via configuração externa
- nunca hardcodar token, senha ou URL sensível

### 14. O que evitar
- banco de dados
- cache distribuído
- eventos assíncronos
- reflection desnecessária
- abstrações genéricas demais
- múltiplos módulos
- domain layer pesada sem necessidade

## Critério de decisão

Quando houver dúvida entre duas abordagens, escolher a que:
1. reduz complexidade
2. melhora clareza
3. mantém a integração com ERP isolada
4. facilita evolução incremental
