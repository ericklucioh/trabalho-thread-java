# Estratégias de Busca

Este documento descreve as estratégias que fazem sentido no projeto atual.

## Estratégias ativas

- `line_by_line`
- `char_by_char`
- `regex`

## Formas de armazenamento

- leitura direta do arquivo
- memória por lista de linhas
- memória por texto inteiro

## Papel da estratégia

A estratégia define como a linha é comparada com o alvo.

Ela não define:

- o dataset
- a quantidade de threads
- o modo de exibição

## Papel do paralelismo

O paralelismo só distribui o trabalho.

No desenho atual:

- o trabalho é dividido em blocos de linhas
- cada bloco pertence a um arquivo
- o bloco não cruza fronteira de arquivo
- a quantidade de threads define o tamanho dos blocos úteis

## Regra prática

Para este projeto, vale priorizar:

1. clareza
2. previsibilidade
3. facilidade de teste
4. bom suficiente para medir `speedup`

## Observação

O projeto já não depende de `MODE`, `TYPE` ou `RESULT_DIR` como contrato principal.
Esses nomes eram úteis no rascunho, mas não são mais a melhor forma de explicar o sistema atual.
