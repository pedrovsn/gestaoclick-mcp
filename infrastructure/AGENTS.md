# infrastructure/AGENTS.md

## Escopo

A pasta `infrastructure` contém todo o código necessário para deploy da aplicação.

Fase inicial:
- deploy no Railway

## Objetivos
- manter infraestrutura simples
- permitir deploy reproduzível
- externalizar configuração
- evitar lock-in e complexidade desnecessária

## Regras obrigatórias

### 1. Responsabilidade da pasta
Tudo aqui deve estar relacionado a:
- empacotamento
- variáveis de ambiente
- configuração de deploy
- scripts operacionais
- CI/CD, se necessário
- observabilidade básica de execução

Não colocar lógica de negócio nesta pasta.

### 2. Railway primeiro
Como o deploy inicial será no Railway:
- priorizar arquivos e configurações compatíveis com Railway
- manter setup mínimo necessário para subir a aplicação
- documentar claramente variáveis obrigatórias

### 3. Simplicidade
- Não criar infraestrutura para Kubernetes nesta fase.
- Não criar Terraform complexo agora, a menos que haja uso real imediato.
- Não introduzir múltiplos ambientes sofisticados sem necessidade.
- Não adicionar componentes que o projeto ainda não usa.

### 4. Configuração
Toda configuração sensível deve vir de variáveis de ambiente.

Exemplos esperados:
- `SERVER_PORT`
- `ERP_BASE_URL`
- `ERP_API_KEY` ou equivalente
- `ERP_TIMEOUT_MS`
- `JAVA_OPTS`
- outras variáveis estritamente necessárias

### 5. Artefatos esperados
Dependendo da estratégia escolhida, esta pasta pode conter:
- `README.md` com instruções de deploy
- `railway.json` se necessário
- `Dockerfile`
- scripts de build/deploy
- exemplos de `.env.example`

Preferir poucos arquivos, bem documentados.

### 6. Docker
Se usar Docker:
- imagem pequena
- build claro
- sem ferramentas desnecessárias
- expor apenas o necessário
- não armazenar secrets na imagem

### 7. CI/CD
Se houver pipeline:
- mantê-lo simples
- buildar
- testar
- publicar/deployar
- evitar automações complexas antes da necessidade real

### 8. Logs e saúde
Garantir o mínimo operacional:
- app sobe corretamente no Railway
- logs vão para stdout/stderr
- endpoint de health disponível
- configuração por env vars

### 9. O que evitar
- banco provisionado sem necessidade
- filas
- caches
- redes complexas
- módulos de observabilidade avançada
- IaC excessivo para um deploy simples

## Regra de ouro

A infraestrutura deve existir para viabilizar o backend, não para reinventar a plataforma.
Escolher sempre a opção mais simples que funcione bem.
