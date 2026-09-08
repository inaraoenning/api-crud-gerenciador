package pessoas

import model.Fornecedor

class FornecedorController(private val repositorio: PessoaRepository) {

    fun executar() {
        while (true) {
            println("--- Gerenciamento de Fornecedores ---")
            println("1 - Cadastrar Fornecedor")
            println("2 - Listar Fornecedores")
            println("3 - Inativar Fornecedor")
            println("0 - Voltar")
            print("Opcao: ")

            when (readln()) {
                "1" -> cadastrarFornecedor()
                "2" -> listar()
                "3" -> inativar()
                "0" -> break
                else -> println("Opcao invalida.")
            }
        }
    }

    private fun cadastrarFornecedor() {
        val dados = lerDadosComunsPessoa() ?: return

        print("Razao Social (ou Enter para usar o nome): ")
        val razaoSocial = readln().ifBlank { dados.nome }

        val fornecedor = Fornecedor(
            idFornecedor = 0,
            nomeFornecedor = dados.nome,
            documentoFornecedor = dados.documento,
            telefoneFornecedor = dados.telefone,
        )

        val ok = repositorio.inserir(fornecedor, razaoSocial = razaoSocial)
        println(if (ok) "Fornecedor cadastrado com sucesso!" else "Erro ao cadastrar fornecedor.")
    }

    private fun listar() {
        repositorio.listarAtivos()
            .filterIsInstance<Fornecedor>()
            .forEach {
                println("${it.id} - ${it.nome} | Documento: ${it.documento} | Telefone: ${it.telefone}")
            }
    }

    private fun inativar() {
        print("ID do fornecedor: ")
        val id = readln().toIntOrNull()
        if (id == null) {
            println("ID invalido.")
            return
        }
        val ok = repositorio.inativar(id)
        println(if (ok) "Fornecedor inativado." else "Fornecedor nao encontrado.")
    }
}
