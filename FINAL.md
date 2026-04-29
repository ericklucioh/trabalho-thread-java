## FINAL

Documento consolidado do trabalho, usando a estrutura de variáveis explícitas que ficou mais natural no rascunho `VARIACOES.md`.

## 1. Objetivo do trabalho

Desenvolver em Java um sistema para buscar um nome dentro de arquivos `.txt` de um diretório, informando:

- nome encontrado
- arquivo onde foi encontrado
- linha da ocorrência
- tempo de execução
- comparação entre versão sequencial e versão paralela

O trabalho pede dois sistemas principais:

- um sistema sequencial, sem paralelismo
- um sistema paralelo, usando `Threads`

O resultado final também deve permitir análise de `speedup`.

## 2. Ideia geral do projeto

O projeto precisa suportar dois usos:

1. modo manual, para executar uma combinação específica de busca e um nome
2. modo automático, para rodar várias combinações e gerar relatório final

Esse segundo modo é o que torna o trabalho realmente útil para análise.

## 3. Variáveis de execução

O sistema deve ser configurado de forma explícita, sem depender só de números soltos.

As variáveis centrais são:

- `IS_PARALLEL`: define se a execução usa threads
- `IS_IN_MEMORY`: define se a busca usa dados carregados em memória
- `FINDER`: define qual algoritmo de busca será usado
- `RESULT_DIR`: diretório onde ficam resultados e métricas

### 3.1 `IS_PARALLEL`

Define o modo de execução.

Valores:

- `false`: execução sequencial
- `true`: execução paralela com `Threads`

Esse é o equivalente prático do que antes aparecia como `sync` e `async`/`parallel`.

### 3.2 `IS_IN_MEMORY`

Define se a busca trabalha com o conteúdo carregado em memória.

Valores:

- `false`: leitura direta do arquivo original
- `true`: uso de estrutura em memória antes da busca

Essa variável serve para comparar custo de leitura com custo de processamento.

### 3.3 `FINDER`

Define o algoritmo de comparação usado para localizar o nome.

Valores:

- `line_by_line`: compara linha por linha
- `char_by_char`: compara caractere por caractere
- `regex`: usa expressão regular

Esses três valores descrevem a forma da busca sem esconder a intenção por trás de um número.

### 3.4 `RESULT_DIR`

Diretório usado para salvar:

- tempo total por execução
- combinação executada
- speedup
- resultados auxiliares do benchmark

## 4. Leitura das combinações

A combinação das variáveis define a variação concreta da execução.

Exemplos de leitura:

- `IS_PARALLEL=false`, `IS_IN_MEMORY=false`, `FINDER=line_by_line`
  - busca sequencial mais direta
- `IS_PARALLEL=true`, `IS_IN_MEMORY=false`, `FINDER=line_by_line`
  - mesma busca, mas distribuída em threads
- `IS_PARALLEL=false`, `IS_IN_MEMORY=true`, `FINDER=regex`
  - busca em memória com regex

O ponto é que a variação seja legível de imediato.

## 5. Estratégias de busca

### 5.1 `FINDER=line_by_line`

Leitura sequencial de cada arquivo, linha a linha, procurando o nome alvo.

Esse deve ser o caminho mais simples e a principal base de comparação.

### 5.2 `FINDER=char_by_char`

Compara manualmente os caracteres da string.

Esse tipo existe para:

- explicitar um algoritmo mais controlado
- permitir estudo de custo de comparação
- evitar depender de atalhos prontos da biblioteca

### 5.3 `FINDER=regex`

Usa expressão regular para localizar ocorrências.

Esse tipo é útil quando:

- a busca precisa aceitar padrões mais flexíveis
- o comportamento precisa ser comparado com buscas mais simples

## 6. Estrutura em memória

`IS_IN_MEMORY` não cria uma nova estratégia isolada.

Ele descreve como a estratégia trabalha:

- `false`: leitura e comparação direto no arquivo
- `true`: conteúdo carregado antes da busca

Se depois vocês quiserem diferenciar formas de carregamento em memória, isso pode virar uma expansão do modelo, mas sem perder o contrato principal.

## 7. Como o paralelismo entra

O paralelismo não troca o algoritmo.
Ele só muda a forma de execução.

Formas práticas:

- paralelismo por arquivo
- paralelismo por bloco de linhas
- paralelismo em pipeline

Leitura consolidada:

- `IS_PARALLEL` define se roda sequencial ou concorrente
- `IS_IN_MEMORY` define a forma de preparação dos dados
- `FINDER` define o algoritmo de busca

## 8. Runner

O runner deve ser a unidade usada para rodar experimentos.

Responsabilidades:

- receber `IS_PARALLEL`, `IS_IN_MEMORY` e `FINDER`
- receber o nome buscado
- executar uma combinação por vez
- registrar o resultado
- registrar o tempo
- apoiar execuções em lote via benchmark

O runner precisa aceitar parâmetros diretamente, sem depender só de variável de ambiente.

## 9. Modo manual

O modo manual serve para executar uma combinação específica.

Exemplo de leitura:

- rodar uma busca sequencial simples
- rodar a mesma busca com threads
- trocar apenas o algoritmo de busca

Esse modo é o mais útil para validar comportamento funcional.

## 10. Modo automático

O modo automático executa várias combinações e gera o relatório final.

Ele deve:

- executar todas as variações possíveis
- repetir cada variação com 50 nomes diferentes
- calcular médias
- registrar tempos e `speedup`
- permitir comparação entre combinações

Esse é o modo mais importante para a análise final do trabalho.

## 11. Benchmark automatizado

O benchmark é a parte que transforma o projeto em algo repetível.

Ele deve:

- rodar várias combinações das variáveis
- repetir cada combinação com 50 nomes diferentes
- calcular média de tempo
- calcular `speedup`
- gerar um relatório final

Fórmula básica:

```text
speedup = tempo_sequencial / tempo_paralelo
```

## 12. Métricas úteis

Além da resposta funcional, o sistema deve guardar:

- tempo total em milissegundos
- número de arquivos lidos
- número de linhas analisadas
- quantidade de threads usadas
- combinação executada
- `speedup`

## 13. Regras práticas de execução

### 13.1 `make`

Exemplos esperados:

- `make run IS_PARALLEL=false IS_IN_MEMORY=false FINDER=line_by_line`
- `make run IS_PARALLEL=true IS_IN_MEMORY=false FINDER=line_by_line`
- `make benchmark`

### 13.2 Configuração

`.env` e `.env.example` devem guardar apenas caminhos de dataset e saída.

Não devem guardar:

- decisão de algoritmo
- decisão de paralelismo
- parâmetros de benchmark

### 13.3 Saída

A saída da execução deve ser previsível e fácil de registrar em relatório.

Deve informar:

- nome encontrado
- arquivo
- linha
- tempo
- combinação usada

## 14. Organização do trabalho

Ordem prática de implementação:

1. definir o contrato de busca
2. definir as variáveis de variação
3. manter a execução explícita por parâmetros
4. garantir execução manual por combinação
5. implementar as estratégias adicionais
6. registrar métricas em `RESULT_DIR`
7. criar benchmark automatizado
8. gerar relatório final com médias e `speedup`

## 15. Estado do projeto observado

O projeto já tem uma base inicial com:

- versão sequencial
- versão paralela
- leitura de arquivos por diretório
- seleção entre execução sequencial e paralela

O próximo passo é evoluir essa base para suportar:

- variáveis explícitas de busca
- benchmark automatizado
- relatório final
- múltiplas combinações comparáveis

## 16. Riscos e cuidados

- não assumir que paralelismo sempre melhora o tempo
- evitar criar threads demais sem necessidade
- evitar duplicação de lógica entre sequencial e paralelo
- garantir ordenação determinística quando necessário
- tratar diretórios ausentes e arquivos inválidos
- não deixar a configuração final presa apenas em variáveis de ambiente

## 17. Resumo final

O trabalho deve ser entendido assim:

- existe uma busca funcional em Java sobre arquivos `.txt`
- existe uma versão sequencial e uma versão paralela
- existe um conjunto de variações definidas por variáveis explícitas
- existe um modo manual e um modo automático
- existe um benchmark que roda várias variações e calcula médias
- existe a necessidade de um relatório final com análise de `speedup`

Em termos práticos, a ideia certa não é só "buscar um nome".  
A ideia certa é "buscar um nome com variações explícitas, em dois modos de execução, com experimentos repetíveis e relatório comparativo".
