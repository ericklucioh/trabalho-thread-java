# Trabalho Thread Java

Setup inicial do trabalho em Java com Docker e Docker Compose.

## Estrutura

- `dataset_g/` e `dataset_p/`: datasets fornecidos no enunciado
- `src/main/java/`: implementação da busca
- `Dockerfile`: imagem da aplicação
- `docker-compose.yml`: execução com os datasets montados como volume somente leitura

## Executar a interface com Docker Compose

Antes de abrir a janela pela primeira vez, permita o acesso do container ao seu servidor X:

```bash
make gui-allow
```

Se o comando `xhost` não existir no host, instale a ferramenta equivalente do seu sistema.

```bash
docker compose up --build
```

O container `app` abre a interface Swing diretamente.

## Modo dev com recarga

Para recompilar e rerodar automaticamente quando `src/main/java` mudar:

```bash
docker compose up --build dev
```

O modo de desenvolvimento recompila com `javac` dentro do container e reabre a interface Swing.

## Executar a interface no container

```bash
docker compose run --rm --build app
```
