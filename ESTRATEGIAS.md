# Estratégias de Busca

Este documento organiza as ideias de busca síncrona e assíncrona em uma estrutura única.
A intenção é separar três coisas que estavam misturadas nas anotações:

- `MODE`: como executar
- `TYPE`: qual estratégia usar
- `RESULT_DIR`: onde salvar métricas e saídas auxiliares

## Objetivo

Encontrar um nome dentro dos arquivos `.txt` dos datasets, registrando:

- nome encontrado
- arquivo
- linha
- tempo de execução
- comparação entre versões síncronas e paralelas

## Conceito central

A mesma estratégia pode existir em dois modos:

- síncrono: um fluxo único executa a busca
- assíncrono/paralelo: vários fluxos executam a mesma lógica em partes diferentes do trabalho

Ou seja:

- a estratégia define o algoritmo
- o modo define a forma de execução

## Proposta de contratos

### `MODE`

Define o modelo de execução.

- `sync`: execução sequencial
- `async` ou `parallel`: execução concorrente com threads

### `TYPE`

Define a estratégia concreta de busca.

Exemplo de mapa inicial:

- `TYPE=1`: busca linha por linha
- `TYPE=2`: carrega arquivo em memória e busca na lista de linhas
- `TYPE=3`: busca por regex
- `TYPE=4`: busca por comparação manual de caracteres
- `TYPE=5`: (somente asinc) estratégia composta em duas fases

### `RESULT_DIR`

Diretório para salvar resultados, logs de experimento ou medições.

Exemplo de uso:

- salvar tempo total por execução
- salvar speedup
- salvar número de arquivos analisados
- salvar número de linhas varridas

## Matriz de estratégias

### Legenda

- `OK`: faz sentido e é recomendada
- `OK*`: faz sentido, mas depende da implementação
- `N/A`: não é uma combinação útil

| TYPE | Estratégia | Sync | Async | Observações |
| --- | --- | --- | --- | --- |
| 1 | Linha por linha | OK | OK | Base mais simples e útil como referência |
| 2 | Arquivo inteiro em memória | OK | OK | Bom para comparar custo de I/O vs CPU |
| 3 | Regex | OK | OK | Útil quando o padrão de busca for mais flexível |
| 4 | Comparação por caractere | OK | OK | Boa para experimentar otimizações manuais |
| 5 | Pipeline em duas fases | OK* | OK | Melhor candidato para composição e paralelismo |

## Descrição das estratégias

### TYPE 1 - Linha por linha

Leitura sequencial de cada arquivo, linha a linha, comparando cada linha com o alvo.

Vantagens:

- simples de implementar
- fácil de testar
- base boa para medir speedup

Desvantagens:

- não explora otimizações avançadas
- pode ser lento em arquivos grandes

### TYPE 2 - Arquivo inteiro em memória

Lê o arquivo todo para uma estrutura em memória e percorre a coleção de linhas depois.

Vantagens:

- código simples
- útil para experimentar custo de alocação e leitura

Desvantagens:

- maior consumo de memória
- pode piorar em datasets grandes

### TYPE 3 - Regex

Usa expressão regular para localizar ocorrências.

Vantagens:

- flexível
- aceita padrões mais complexos que igualdade simples

Desvantagens:

- pode ser mais custosa
- precisa de cuidado com escape e performance

### TYPE 4 - Comparação por caractere

Faz a comparação manual dos caracteres da linha com o nome buscado.

Vantagens:

- abre espaço para otimizações específicas
- útil para estudo de algoritmo

Desvantagens:

- mais código
- maior chance de erro

### TYPE 5 - Pipeline em duas fases

Estratégia composta.

Exemplo:

- fase 1: filtra candidatos por primeiro caractere ou prefixo
- fase 2: valida a string completa

Vantagens:

- boa para paralelismo
- permite dividir trabalho em etapas
- útil para estudar speedup real

Desvantagens:

- maior complexidade
- exige coordenação entre fases

## Como o paralelismo entra

O paralelismo não substitui a estratégia.
Ele distribui a execução da estratégia.

Formas práticas:

### Paralelismo por arquivo

Cada thread processa um arquivo.

Bom para:

- datasets com muitos arquivos
- estratégias simples como linha por linha ou arquivo inteiro

### Paralelismo por bloco

Cada thread processa um bloco de linhas.

Bom para:

- arquivos grandes
- comparar escalabilidade de CPU

### Paralelismo em pipeline

Uma thread ou grupo de threads faz o pré-filtro, e outra fase valida.

Bom para:

- estratégias compostas
- experimentos de arquitetura concorrente

## Recomendações de implementação

### Estrutura de código

Separar em três camadas:

1. seleção da estratégia
2. modo de execução
3. escrita de resultado/métrica

### Interfaces sugeridas

Uma forma simples de organizar:

- `SearchStrategy`
  - define a busca
- `SearchExecutor`
  - executa a estratégia em sync ou async
- `SearchResultWriter`
  - salva métricas e resultados

### Enumeração do tipo

Exemplo de papel do `TYPE`:

- `TYPE=1` -> `LineByLineStrategy`
- `TYPE=4` -> `CharByCharStrategy`
- `TYPE=3` -> `RegexStrategy`
  
- `TYPE=2` -> `ReadingOriginalFile`
- `TYPE=2` -> `InMemoryStrategyListStringByLine`
- `TYPE=2` -> `InMemoryStrategyListStringByFile`

- `TYPE=5` -> `PipelineStrategy`

### Modo de execução

Exemplo:

- `MODE=sync` -> executa a estratégia diretamente
- `MODE=async` -> distribui a estratégia entre threads

## Combinações recomendadas para teste

Comece com estas combinações:

- `TYPE=1 MODE=sync`
- `TYPE=1 MODE=async`
- `TYPE=2 MODE=sync`
- `TYPE=2 MODE=async`
- `TYPE=5 MODE=async`

Essas combinações já permitem:

- comparar custo de leitura
- comparar custo de coordenação
- medir speedup

## Métricas úteis

Além do resultado funcional, vale registrar:

- tempo total em milissegundos
- número de arquivos lidos
- número de linhas analisadas
- quantidade de threads usadas
- speedup em relação ao sync

Fórmula básica:

```text
speedup = tempo_sync / tempo_async
```

## Riscos e cuidados

- Não assumir que paralelismo sempre melhora tempo.
- Evitar criar threads demais sem necessidade.
- Garantir ordem determinística de saída quando necessário.
- Tratar arquivos ausentes ou diretórios inválidos.
- Evitar duplicar lógica entre sync e async.

## Leitura prática do projeto atual

Hoje o projeto já tem uma boa base para isso:

- leitura de datasets por diretório
- versão sequencial
- versão paralela
- `SearchMode` para escolher entre `sync` e `parallel`

O próximo passo lógico é adicionar a noção de `TYPE`.

## Proposta de evolução

1. Transformar o contrato de busca em uma estratégia explícita.
2. Fazer `TYPE` escolher a estratégia.
3. Manter `MODE` como seletor de execução.
4. Escrever métricas em `RESULT_DIR`.
5. Rodar benchmarks com as combinações mais relevantes.

## Resumo final

Sim, suas ideias fazem sentido.
O ajuste importante é este:

- `estratégias gerais` servem para sync e async
- o que muda é a forma de execução
- `TYPE` deve escolher o algoritmo
- `MODE` deve escolher se ele roda sequencialmente ou em paralelo

Esse modelo evita duplicação mental e facilita experimentar várias versões sem bagunçar o código.
