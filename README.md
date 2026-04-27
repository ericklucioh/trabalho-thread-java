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
java -jar app.jar sync "Sharon Sullivan"
```

Para testar outro nome:

```bash
docker compose run --rm app parallel "Karen Reyes MD"
```

## Executar localmente

```bash
mvn -q -DskipTests package
java -jar target/trabalho-thread-java-1.0.0-SNAPSHOT.jar sync "Sharon Sullivan"
```
