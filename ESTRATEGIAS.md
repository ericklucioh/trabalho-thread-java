# Estratégias de Busca

Este documento organiza as ideias de busca síncrona e assíncrona em uma estrutura única.
A intenção é separar três coisas que estavam misturadas nas anotações:

- `TYPE`: qual estratégia usar
- `MODE`: como executar a estratégia
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

## Estado atual da configuração

Hoje a configuração prática ficou assim:

- `.env` e `.env.example` guardam apenas os diretórios
- `MODE` não precisa ficar em variável de ambiente
- `TYPE` deve ser passado na chamada do runner ou do `make`
- `benchmark` deve disparar o runner várias vezes, uma por estratégia

Exemplos atuais:

- `make sync TYPE=1`
- `make async TYPE=2`
- `make benchmark TYPES="1 2 3 4 5"`

## Proposta de contratos

### `MODE`

Define o modelo de execução.

- `sync`: execução sequencial
- `async` ou `parallel`: execução concorrente com threads

Observação:

- `MODE` não precisa ser persistido no `.env`
- ele pode ser um argumento de execução, um parâmetro do runner ou um alvo do `make`

### `TYPE`

Define a estratégia concreta de busca.

Exemplo de mapa inicial:

- `TYPE=1`: busca linha por linha
- `TYPE=2`: família de estratégias em memória
- `TYPE=3`: busca por regex
- `TYPE=4`: busca por comparação manual de caracteres
- `TYPE=5`: estratégia composta em duas fases

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
| 2 | Família de estratégias em memória | OK | OK | Pode ter subtipos internos |
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

### TYPE 2 - Família de estratégias em memória

O `TYPE=2` representa uma família de variações em memória, não uma única implementação.

Subtipos possíveis:

- `ReadingOriginalFile`
  - lê o arquivo original e processa o conteúdo diretamente
- `InMemoryStrategyListStringByLine`
  - carrega cada arquivo em `List<String>` e busca linha a linha
- `InMemoryStrategyListStringByFile`
  - carrega ou organiza a busca em estruturas em memória por arquivo

Vantagens:

- permite comparar custo de leitura e custo de processamento
- abre espaço para medir diferenças entre representações internas

Desvantagens:

- aumenta a complexidade do `TYPE=2`
- exige um contrato claro para diferenciar os subtipos

### TYPE 2.1 - ReadingOriginalFile

Lê o arquivo original e processa o conteúdo sem uma estrutura intermediária explícita.

### TYPE 2.2 - InMemoryStrategyListStringByLine

Carrega o arquivo em memória como lista de linhas e depois aplica a busca.

### TYPE 2.3 - InMemoryStrategyListStringByFile

Carrega a estrutura pensando mais no arquivo como unidade de processamento.

O comportamento original de ler tudo em memória continua válido e deve ser tratado como uma das variações possíveis dentro da família `TYPE=2`, não como uma estratégia isolada.

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

### Runner

O runner deve ser a unidade usada para rodar uma sequência de experimentos.

Responsabilidades:

- receber `TYPE` e `MODE`
- executar uma combinação por vez
- registrar tempo e resultado
- permitir múltiplas chamadas em sequência por `benchmark`

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

O próximo passo lógico é adicionar a noção de `TYPE` e um runner que consiga ser acionado repetidas vezes pelo `make benchmark`.

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
