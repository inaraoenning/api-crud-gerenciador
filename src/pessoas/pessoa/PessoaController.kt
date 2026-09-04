package pessoas.pessoa

import lerInteiroSeguro
import pessoas.cliente.ClienteController
import pessoas.fornecedor.FornecedorController
import pessoas.funcionario.FuncionarioController

class PessoaController(private val repositorio: PessoaRepository) {

    fun executar() {
        while (true) {
            println("--- Gerenciamento de Pessoas ---")
            println("1 - Funcionarios")
            println("2 - Fornecedores")
            println("3 - Clientes")
            println("4 - Listar Todas as Pessoas")
            println("5 - Inativar Pessoa")
            println("0 - Voltar")
            print("Opcao: ")

            when (readln()) {
                "1" -> FuncionarioController(repositorio).executar()
                "2" -> FornecedorController(repositorio).executar()
                "3" -> ClienteController(repositorio).executar()
                "4" -> listar()
                "5" -> inativar()
                "0" -> break
                else -> println("Opcao invalida.")
            }
        }
    }

    private fun listar() {
        val pessoas = repositorio.listarAtivos()
        if (pessoas.isEmpty()) {
            println("Nenhuma pessoa cadastrada.")
            return
        }
        pessoas.forEach { println("${it.id} - ${it.nome} [${it.tipo}]") }
    }

    private fun inativar() {
        val id = lerInteiroSeguro("ID da pessoa") ?: return
        val ok = repositorio.inativar(id)
        println(if (ok) "Pessoa inativada." else "Pessoa nao encontrada.")
    }
}
