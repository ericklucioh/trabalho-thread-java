# Trabalho Thread Java

Setup inicial do trabalho em Java com Docker e Docker Compose.

## Estrutura

- `dataset_g/` e `dataset_p/`: datasets fornecidos no enunciado
- `src/main/java/`: implementação da busca
- `Dockerfile`: imagem da aplicação
- `docker-compose.yml`: execução com os datasets montados como volume somente leitura

## Executar com Docker Compose

```bash
docker compose up --build
```

O `command` padrão no `docker-compose.yml` está configurado para:

```bash
java -jar app.jar "$MODE" "$TARGET"
```

Defina `MODE` e `TARGET` no `.env` antes de subir.

```bash
docker compose run --rm app
```

## Modo dev com recarga

Para recompilar e rerodar automaticamente quando `src/main/java` mudar:

```bash
docker compose up --build dev
```

Você pode mudar o alvo sem editar o compose:

```bash
TARGET="<target>" docker compose up --build dev
```

## Executar localmente

```bash
mvn -q -DskipTests package
java -jar target/trabalho-thread-java-1.0.0-SNAPSHOT.jar sync "$TARGET"
```
