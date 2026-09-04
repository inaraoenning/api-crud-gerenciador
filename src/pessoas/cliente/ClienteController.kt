package pessoas.cliente

import lerDoubleSeguro
import model.Cliente
import pessoas.pessoa.PessoaRepository
import pessoas.pessoa.lerDadosComunsPessoa

class ClienteController(private val repositorio: PessoaRepository) {

    fun executar() {
        while (true) {
            println("--- Gerenciamento de Clientes ---")
            println("1 - Cadastrar Cliente")
            println("2 - Listar Clientes")
            println("3 - Inativar Cliente")
            println("0 - Voltar")
            print("Opcao: ")

            when (readln()) {
                "1" -> cadastrarCliente()
                "2" -> listar()
                "3" -> inativar()
                "0" -> break
                else -> println("Opcao invalida.")
            }
        }
    }

    private fun cadastrarCliente() {
        val dados = lerDadosComunsPessoa() ?: return

        val limiteCredito = lerDoubleSeguro("Limite de Credito") ?: 0.0

        val cliente = Cliente(
            idCliente = 0,
            nomeCliente = dados.nome,
            documentoCliente = dados.documento,
            telefoneCliente = dados.telefone,
        )

        val ok = repositorio.inserir(cliente, limiteCredito = limiteCredito)
        println(if (ok) "Cliente cadastrado com sucesso!" else "Erro ao cadastrar cliente.")
    }

    private fun listar() {
        repositorio.listarAtivos()
            .filterIsInstance<Cliente>()
            .forEach {
                println("${it.id} - ${it.nome} | Documento: ${it.documento} | Telefone: ${it.telefone}")
            }
    }

    private fun inativar() {
        print("ID do cliente: ")
        val id = readln().toIntOrNull()
        if (id == null) {
            println("ID invalido.")
            return
        }
        val ok = repositorio.inativar(id)
        println(if (ok) "Cliente inativado." else "Cliente nao encontrado.")
    }
}
