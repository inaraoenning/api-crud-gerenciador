import pessoas.pessoa.PessoaRepository

fun main() {

    val pessoas = PessoaRepository.getInstancia()
    //val itens = ItemRepository.getInstancia()
    //val vendas = VendaRepository.getInstancia()
    //val financeiro = FinanceiroRepository.getInstancia()

    //val pessoaController = PessoaController(pessoas)
    //val estoqueController = EstoqueController(itens)
    //val vendaController = VendaController(vendas, itens, pessoas)
    //val financeiroController = FinanceiroController(pessoas, itens, financeiro)

    //Logger.info("Sistema iniciado.")

    while (true) {
        println("==== SISTEMA CAIXAS D'AGUA ====")
        println("1 - Gerenciar Usuários")
        println("2 - Gerenciar Estoque")
        println("3 - Vendas")
        println("4 - Financeiro")
        println("0 - Sair")

        print("Escolha uma opcao: ")
        val opcao = readln()

        when (opcao) {
            "1" -> menuPessoas()
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

fun menuPessoas() {
    println("--- Gerenciar Usuários ---")
    println("1 - Cadastrar Funcionario")
    println("2 - Cadastrar Fornecedor")
    println("3 - Cadastrar Cliente")
    println("4 - Voltar")
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