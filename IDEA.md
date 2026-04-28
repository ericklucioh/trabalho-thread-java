classe search mode abstract ou interface
- nome
loggers padrao
func para override cada um vai ter o seu tipo

escopo geral,
testar em memoria, carregar todo o arquivo em lista
procurar no arquivo diretamente,
uma string pra cada arquivo
por regex, por caracter, por string

# estrategias gerais

1. procurar linha por linha se bate os valores
2. primeiro bater apenas o primeiro caracter de cada linha, se igual comparar o proximo, se os 2 forem corretos, comparar a linha inteira, se nao, continua

## com paralelismo

X theads diferentes, nos metodos acima

metodo composto, X threads procuram os q começam o primeiro caractere, se achar fala aonde fica, e continua procurando outros com o mesmo caractere
e outros y threads ficam ouvindo essa lista e se tiver algo nov validam a string inteira