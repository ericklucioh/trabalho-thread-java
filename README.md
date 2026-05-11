# Trabalho Thread Java

Aplicacao em Java 17 com interface Swing para buscar nomes em arquivos `.txt` dos datasets do trabalho.

## Visao Geral

- a interface Swing e a entrada principal
- o caminho CLI foi removido
- a busca suporta:
  - `line_by_line`
  - `char_by_char`
  - `regex`
- a execucao suporta:
  - leitura direta do arquivo
  - memoria por lista de linhas
  - memoria por texto inteiro
- o paralelismo divide o trabalho em blocos de linhas
- o resultado mostra nome, arquivo, linha e tempo de execucao

## Estrutura

- `dataset_g/` e `dataset_p/`: datasets do trabalho
- `src/main/java/thread/ui/`: interface Swing, controllers e views
- `src/main/java/thread/search/`: motor de busca, estrategias e particionamento
- `src/test/java/`: testes automatizados
- `scripts/`: scripts de compilacao e empacotamento
- `Dockerfile`, `Dockerfile.dev` e `docker-compose.yml`: execucao em container

## Como Executar

Com Docker:

```bash
docker compose up --build
```

Ou com o alvo do Makefile:

```bash
make run
```

## Desenvolvimento

Para recompilar automaticamente enquanto edita:

```bash
docker compose up --build dev
```

Ou:

```bash
make dev
```

## Build e Testes

Com Maven localmente, se voce tiver JDK 17 e Maven instalados:

```bash
mvn test
mvn package
```

No ambiente em container:

```bash
sh ./scripts/compile.sh
sh ./scripts/package.sh
```

## Comandos Uteis

- `make build`: compila no container
- `make package`: gera o jar no container
- `make clean`: remove `target/`
- `make gui-allow`: libera a exibicao de janelas no X local, se necessario

## Observacoes

- a aplicacao usa `thread.ui.UiApp` como classe principal
- os resultados e arquivos gerados podem ser montados em `results/`
- os datasets entram somente leitura no container
