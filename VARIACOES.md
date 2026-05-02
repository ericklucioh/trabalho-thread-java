# Variações do Projeto

Este arquivo registra os eixos reais que fazem sentido no projeto atual.

## Eixo 1: estratégia de busca

- `line_by_line`
- `char_by_char`
- `regex`

## Eixo 2: forma de armazenamento

- leitura direta do arquivo
- memória por lista de linhas
- memória por texto inteiro

## Eixo 3: paralelismo

- quantidade de threads escolhida na UI
- divisão por blocos de linhas
- sem cruzar arquivo no meio do bloco

## Regra atual de paralelismo

- o total de linhas dos datasets selecionados é dividido em blocos
- cada bloco pertence a um único arquivo
- o bloco não cruza fronteira de arquivo
- a quantidade de threads define o tamanho dos blocos úteis

## Observação prática

O objetivo não é multiplicar variações infinitas.
O objetivo é manter o modelo compreensível o bastante para:

- implementar
- testar
- explicar na apresentação
- medir `speedup`
