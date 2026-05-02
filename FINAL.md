# FINAL

Documento consolidado do projeto atual.

## Resumo

Aplicação em Java com interface Swing para buscar nomes em arquivos `.txt` dos datasets do trabalho.

## Regras atuais

- a UI é a entrada principal
- o CLI foi removido
- a busca funciona sobre:
  - `line_by_line`
  - `char_by_char`
  - `regex`
- a execução pode usar:
  - leitura direta do arquivo
  - memória por lista de linhas
  - memória por texto inteiro
- o paralelismo divide o trabalho em blocos de linhas por arquivo

## O que a aplicação entrega

- nome encontrado
- arquivo
- linha
- tempo de execução
- base para comparar sequencial e paralelo

## O que continua importante

- manter o código simples o bastante para apresentar
- preservar o arquivo e a linha no resultado
- medir `speedup`
- continuar usando os datasets do AVA

## Leitura prática

O projeto está organizado em torno de:

1. interface Swing
2. motor de busca
3. estratégias de comparação
4. particionamento para threads
5. histórico e resultado visual
