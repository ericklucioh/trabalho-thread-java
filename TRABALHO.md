# Trabalho Thread Java

Documento oficial do trabalho da disciplina.

## Requisitos centrais

- desenvolver em Java
- buscar um nome em arquivos `.txt` de um diretório
- considerar todos os arquivos do dataset
- informar:
  - nome encontrado
  - arquivo
  - linha
  - tempo de execução
- ter duas versões:
  - sequencial
  - paralela com `Threads`
- analisar `speedup`

## Observações oficiais

- usar os arquivos `.txt` disponibilizados no AVA
- a apresentação e a análise dos resultados contam na nota
- pontos extras podem vir do uso de diferentes algoritmos de pesquisa
- pontos extras podem vir de diferentes formas de uso de `Threads`

## Leitura prática para o projeto atual

- a interface Swing é o ponto de entrada
- a busca sequencial e a paralela devem compartilhar a mesma lógica de domínio
- o paralelismo deve preservar arquivo e linha no resultado
- o projeto pode usar diferentes estratégias de busca como diferencial
