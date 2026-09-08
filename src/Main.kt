import caixadaagua.CaixaDaAguaController
import financeiro.FinanceiroController
import pessoas.PessoaController
import pessoas.PessoaRepository
import servico.ServicoController
import venda.VendaController

// Ponto de entrada da aplicacao.
// Aqui fica o menu principal que delega para cada modulo do sistema.
fun main() {

    // Instancia o repositorio e controller de pessoas.
    val pessoas = PessoaRepository.getInstancia()
    val pessoaController = PessoaController(pessoas)

    // Controllers dos outros modulos.
    val caixaController = CaixaDaAguaController()
    val servicoController = ServicoController()
    val vendaController = VendaController()
    val financeiroController = FinanceiroController()

    while (true) {
        println("\n==== SISTEMA CAIXAS D'AGUA ====")
        println("1 - Gerenciar Pessoas")
        println("2 - Gerenciar Estoque")
        println("3 - Gerenciar Servicos")
        println("4 - Frente de Caixa")
        println("5 - Financeiro")
        println("0 - Sair")

        print("Escolha uma opcao: ")
        val opcao = readln()

        when (opcao) {
            "1" -> pessoaController.executar()
            "2" -> caixaController.executar()
            "3" -> servicoController.executar()
            "4" -> vendaController.executar()
            "5" -> financeiroController.executar()
            "0" -> {
                println("Ate logo!")
                break
            }
            else -> println("Opcao invalida.")
        }
    }
}
