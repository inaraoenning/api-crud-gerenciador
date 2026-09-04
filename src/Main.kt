import pessoas.pessoa.PessoaController
import pessoas.pessoa.PessoaRepository

fun main() {

    val pessoas = PessoaRepository.getInstancia()
    val pessoaController = PessoaController(pessoas)

    while (true) {
        println("==== SISTEMA CAIXAS D'AGUA ====")
        println("1 - Gerenciar Pessoas")
        println("2 - Gerenciar Estoque")
        println("3 - Vendas")
        println("4 - Financeiro")
        println("0 - Sair")

        print("Escolha uma opcao: ")
        val opcao = readln()

        when (opcao) {
            "1" -> pessoaController.executar()
            "2" -> menuEstoque()
            "3" -> menuVendas()
            "4" -> menuFinanceiro()
            "0" -> {
                println("Ate logo!")
                break
            }
            else -> println("Opcao invalida.")
        }
    }
}

fun menuEstoque() {
    println("--- Modulo Estoque ---")
}

fun menuVendas() {
    println("--- Modulo Vendas ---")
}

fun menuFinanceiro() {
    println("--- Modulo Financeiro ---")
}
