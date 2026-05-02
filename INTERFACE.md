# Interface do Projeto

## Objetivo

Criar uma interface simples em Java puro para executar buscas no conjunto de arquivos `.txt` do projeto sem depender de CLI ou configuração manual espalhada.

A tela deve permitir:

- digitar um nome para buscar
- escolher a quantidade de threads
- escolher um ou mais datasets
- escolher uma estratégia de busca
- ver o resultado da busca imediatamente
- consultar o histórico de execuções em formato visual

## Estrutura da tela

### Cabeçalho

O cabeçalho deve concentrar a ação principal da aplicação.

Elementos esperados:

- campo de texto para digitar o nome desejado
- botão de busca no canto superior direito
- botão para abrir a tela de histórico ou visualização de resultados

### Lista de exemplos

Abaixo do cabeçalho deve existir uma lista com cerca de 20 nomes aleatórios para demonstração.

Comportamento esperado:

- ao clicar em um nome da lista, o valor deve preencher o campo de busca
- a lista serve apenas como atalho para testes rápidos e demonstração da interface

### Área de configuração

Os dois terços restantes da página devem concentrar as configurações da busca.

Essa área deve permitir selecionar de forma clara:

- datasets ativos
- quantidade de threads
- estratégia de busca
- forma de armazenamento/leitura

O texto exibido na interface deve ser objetivo e descritivo, sem siglas soltas.

O visual deve ser mais quadrado e funcional do que decorativo, com blocos bem definidos e leitura direta.

## Comportamento da busca

Quando o botão de busca for acionado:

1. a aplicação executa a busca com as configurações escolhidas
2. ao finalizar, exibe uma janela de resultado com o retorno da busca
3. o resultado deve informar, no mínimo:
   - se encontrou ou não
   - nome encontrado
   - arquivo
   - linha
   - tempo de execução

Se houver mais de uma ocorrência, o sistema pode listar todas no mesmo retorno ou apresentar um resumo seguido da listagem completa.

## Histórico de resultados

Ao acionar a visualização de resultados, a interface deve carregar o histórico salvo pelas execuções anteriores.

A apresentação deve ser visual, em formato de tabela, com foco em leitura rápida e comparação entre execuções.

O histórico deve permitir observar:

- nome buscado
- estratégia usada
- quantidade de threads
- modo de armazenamento
- arquivo encontrado
- linha encontrada
- tempo gasto

## Requisitos de usabilidade

- A interface precisa ser simples de entender sem leitura prévia da implementação.
- Os rótulos da tela devem ser descritivos e consistentes.
- A ação principal deve estar sempre visível.
- A visualização de resultado deve evitar excesso de texto técnico.

## Simplificação operacional

Com essa interface pronta, a aplicação deve reduzir a dependência de configuração manual espalhada.

O objetivo é manter apenas fluxos simples de uso:

- abrir a interface
- escolher parâmetros na própria tela
- executar a busca
- consultar histórico

## Resultado esperado

Essa interface deve servir como ponto único de uso do projeto:

- para executar buscas manuais
- para comparar estratégias
- para explorar paralelismo por threads
- para apoiar a análise final do trabalho
