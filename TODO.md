# TODO

Lista prática do que falta para transformar as ideias em execução repetível.

## Prioridade 1

- [ ] Definir um contrato explícito para estratégias de busca
- [ ] Criar um seletor de estratégia baseado em `TYPE`
- [ ] Fazer o runner aceitar `TYPE` por parâmetro, sem depender de variável de ambiente
- [ ] Manter `MODE` apenas como argumento de execução
- [ ] Garantir que `make sync TYPE=...` e `make async TYPE=...` funcionem de ponta a ponta

## Prioridade 2

- [ ] Implementar a família `TYPE=2` com subtipos claros
- [ ] Separar `ReadingOriginalFile`
- [ ] Separar `InMemoryStrategyListStringByLine`
- [ ] Separar `InMemoryStrategyListStringByFile`
- [ ] Implementar `TYPE=3` com regex
- [ ] Implementar `TYPE=4` com comparação por caractere
- [ ] Implementar `TYPE=5` como pipeline em duas fases

## Prioridade 3

- [ ] Registrar métricas em `RESULT_DIR`
- [ ] Salvar tempo total por execução
- [ ] Salvar tipo executado
- [ ] Salvar modo executado
- [ ] Salvar speedup comparando `sync` com `async`
- [ ] Adicionar ordenação determinística nos resultados quando necessário

## Prioridade 4

- [ ] Fazer `make benchmark TYPES="1 2 3 4 5"` rodar uma matriz completa de experimentos
- [ ] Permitir benchmark por subconjunto de tipos
- [ ] Documentar a saída esperada do runner
- [ ] Atualizar README com os comandos reais de execução

## Observações

- `MODE` não deve voltar para o `.env`
- `.env` deve continuar só com caminhos de dataset e saída
- o objetivo principal agora é permitir execuções repetidas com parâmetros diferentes sem alterar configuração global
