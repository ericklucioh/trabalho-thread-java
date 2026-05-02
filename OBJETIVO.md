# Objetivo do Projeto

## Meta principal

Construir uma aplicação em Java que busque nomes em arquivos `.txt` e compare execução sequencial com execução paralela.

## O que precisa existir

- interface Swing como ponto único de uso
- busca em datasets selecionáveis
- escolha de estratégia de busca
- escolha de quantidade de threads
- retorno com:
  - nome encontrado
  - arquivo
  - linha
  - tempo
- base para cálculo de `speedup`

## Estratégias que fazem sentido no projeto

- `line_by_line`
- `char_by_char`
- `regex`

## Formas de execução

- leitura direta do arquivo
- memória por lista de linhas
- memória por texto inteiro

## Regra prática

- começar com o essencial funcionando
- expandir depois com mais variações e métricas
- evitar dependência de variáveis de ambiente para o uso diário
