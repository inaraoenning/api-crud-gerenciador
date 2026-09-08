package servico

import utils.lerDoubleSeguro
import utils.lerInteiroSeguro
import model.Servico

// Controller do modulo de Servicos.
// Exibe o menu e le os dados do usuario para cadastrar, listar,
// atualizar ou remover servicos oferecidos pela loja.
class ServicoController {

    fun executar() {
        while (true) {
            println("\n--- Gerenciamento de Servicos ---")
            println("1 - Cadastrar Servico")
            println("2 - Listar Servicos")
            println("3 - Editar Servico")
            println("4 - Deletar Servico")
            println("0 - Voltar")
            print("Opcao: ")

            when (readln()) {
                "1" -> cadastrar()
                "2" -> listar()
                "3" -> editar()
                "4" -> deletar()
                "0" -> break
                else -> println("Opcao invalida.")
            }
        }
    }

    private fun cadastrar() {
        print("Nome do servico: ")
        val nome = readln()
        print("Descricao: ")
        val descricao = readln()
        val preco = lerDoubleSeguro("Preco") ?: return

        val servico = Servico(id = 0, nome = nome, descricao = descricao, preco = preco)
        val id = ServicoRepository.inserir(servico)
        println("Servico cadastrado com sucesso! ID: $id")
    }

    private fun listar() {
        val servicos = ServicoRepository.listar()
        if (servicos.isEmpty()) {
            println("Nenhum servico cadastrado.")
            return
        }

        servicos.forEach {
            println("ID: ${it.id} | ${it.nome} | R$ ${it.preco} | ${it.descricao}")
        }
    }

    private fun editar() {
        val id = lerInteiroSeguro("ID do servico") ?: return
        val servicoAtual = ServicoRepository.buscarPorId(id)

        if (servicoAtual == null) {
            println("Servico nao encontrado.")
            return
        }

        println("Deixe em branco para manter o valor atual.")
        print("Nome [${servicoAtual.nome}]: ")
        val nome = readln().ifBlank { servicoAtual.nome }
        print("Descricao [${servicoAtual.descricao}]: ")
        val descricao = readln().ifBlank { servicoAtual.descricao }
        print("Preco [${servicoAtual.preco}]: ")
        val preco = readln().toDoubleOrNull() ?: servicoAtual.preco

        val atualizado = servicoAtual.copy(nome = nome, descricao = descricao, preco = preco)
        val ok = ServicoRepository.atualizar(atualizado)
        println(if (ok) "Servico atualizado com sucesso!" else "Erro ao atualizar servico.")
    }

    private fun deletar() {
        val id = lerInteiroSeguro("ID do servico") ?: return
        val ok = ServicoRepository.deletar(id)
        println(if (ok) "Servico removido com sucesso!" else "Servico nao encontrado.")
    }
}
