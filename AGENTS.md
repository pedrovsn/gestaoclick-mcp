# AGENTS.md

Este repositório contém duas áreas principais:

- `backend`: API em Kotlin + Spring Boot
- `infrastructure`: código de infraestrutura para deploy

## Objetivo do projeto

Construir um serviço simples para uso com OpenClaw/MCP que consome dados de uma API de ERP e expõe relatórios personalizados.

## Princípios obrigatórios para qualquer agente

1. **Simplicidade primeiro**
   - Este serviço é simples.
   - Não introduza complexidade desnecessária.
   - Evite abstrações genéricas “para o futuro”.
   - Evite padrões sofisticados se um fluxo direto resolver.

2. **Sem banco de dados**
   - Não adicionar PostgreSQL, MySQL, MongoDB, Redis ou qualquer persistência local/remota para dados de domínio.
   - A fonte de verdade é a API do ERP.
   - O serviço apenas consulta, transforma e expõe dados.
   - Só usar armazenamento se houver necessidade estritamente operacional e aprovada explicitamente.

3. **Arquitetura**
   - Usar Kotlin + Spring Boot.
   - Seguir uma abordagem **CQRS simples**, separando claramente leitura de escrita.
   - Como o serviço inicialmente será majoritariamente de leitura, priorizar queries e fluxos de consulta.
   - Commands só devem existir se fizerem sentido para orquestração, refresh, validação ou transformação.
   - Não usar event sourcing.
   - Não usar mensageria.
   - Não criar microsserviços.
   - Não modularizar o projeto prematuramente.

4. **Estrutura do repositório**
   - `backend/`: código da API Kotlin/Spring
   - `infrastructure/`: código de deploy e provisionamento
   - Não mover responsabilidades entre essas duas áreas sem necessidade clara.

5. **Integração com ERP**
   - Toda integração externa deve ficar bem isolada atrás de interfaces/gateways/clients.
   - Modelos da API do ERP não devem vazar desnecessariamente para toda a aplicação.
   - Criar DTOs/mappers quando isso melhorar clareza.
   - Tratar timeouts, erros HTTP e respostas inesperadas de forma explícita.

6. **Endpoints**
   - Os endpoints da nossa API podem espelhar ou reorganizar endpoints do ERP, desde que isso melhore a experiência de consumo.
   - Preferir contratos estáveis e claros.
   - Não expor detalhes internos desnecessários.
   - Sempre favorecer respostas orientadas a relatório/consulta.

7. **Boas práticas de código**
   - Preferir Kotlin idiomático.
   - Preferir funções pequenas e nomes claros.
   - Evitar comentários óbvios.
   - Evitar duplicação.
   - Falhar com mensagens de erro úteis.
   - Manter baixo acoplamento.

8. **Antes de implementar**
   - Sempre confirmar se a mudança pertence a `backend` ou `infrastructure`.
   - Verificar se a solução mantém a simplicidade do projeto.
   - Verificar se não está introduzindo persistência sem autorização.
   - Verificar se a mudança realmente precisa existir agora.

9. **Ao propor mudanças**
   - Explique rapidamente o porquê.
   - Liste trade-offs quando houver.
   - Prefira mudanças pequenas e incrementais.

10. **Não fazer sem alinhamento explícito**
   - adicionar banco de dados
   - adicionar filas/mensageria
   - adicionar cache distribuído
   - criar autenticação complexa
   - criar observabilidade sofisticada além do básico
   - criar múltiplos módulos Gradle
   - introduzir arquitetura hexagonal completa se o ganho não for claro

## Entregas esperadas do agente

Ao trabalhar neste repositório, o agente deve:
- preservar simplicidade
- manter boa separação entre backend e infraestrutura
- seguir CQRS leve
- priorizar legibilidade e manutenção
- evitar overengineering
