# Trabalho Thread Java

Aplicação em Java com interface Swing para buscar nomes em arquivos `.txt` dos datasets do trabalho.

## Estado atual

- a interface Swing é a entrada principal
- o caminho CLI foi removido
- a busca suporta:
  - `line_by_line`
  - `char_by_char`
  - `regex`
- a execução suporta:
  - leitura direta do arquivo
  - memória por lista de linhas
  - memória por texto inteiro
- o paralelismo divide o trabalho em blocos de linhas

## Estrutura do projeto

- `dataset_g/` e `dataset_p/`: datasets do trabalho
- `src/main/java/thread/ui/`: UI Swing, controller e view
- `src/main/java/thread/search/`: motor de busca, estratégias e particionamento
- `Dockerfile` e `docker-compose.yml`: execução em container

## Como executar

```bash
docker compose up --build
```

Isso abre a interface Swing.

## Modo de desenvolvimento

```bash
docker compose up --build dev
```

O container de desenvolvimento recompila a aplicação dentro do ambiente do projeto.

## Validação

Os scripts do projeto compilam e empacotam dentro do container:

```bash
sh ./scripts/compile.sh
sh ./scripts/package.sh
```
