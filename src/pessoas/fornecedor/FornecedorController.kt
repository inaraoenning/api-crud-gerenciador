package pessoas.fornecedor

import enums.MarcaCaixa
import model.Fornecedor
import pessoas.PessoaRepository
import pessoas.lerDadosComunsPessoa
import utils.Validador

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
        val dados = lerDadosComunsPessoa(repositorio)

        var razaoSocial: String
        while (true) {
            print("Razao Social (ou Enter para usar o nome '${dados.nome}'): ")
            razaoSocial = readln().ifBlank { dados.nome }

            if (Validador.razaoSocialValida(razaoSocial)) {
                break
            }

            println("Razão Social contém caracteres inválidos. Tente novamente.")
        }

        println("Marca fornecida: 1-Fortlev 2-Aqualimp 3-Tigre")
        val marca =
            when (readln()) {
                "1" -> MarcaCaixa.Fortlev
                "2" -> MarcaCaixa.Aqualimp
                "3" -> MarcaCaixa.Tigre
                else -> {
                    println("Marca invalida.")
                    return
                }
            }

        val fornecedor =
            Fornecedor(
                idFornecedor = 0,
                nomeFornecedor = dados.nome,
                razaoFornecedor = razaoSocial,
                documentoFornecedor = dados.documento,
                telefoneFornecedor = dados.telefone,
                marca = marca,
            )

        val ok = repositorio.inserir(fornecedor, razaoSocial = razaoSocial, marca = marca)
        println(if (ok) "Fornecedor cadastrado com sucesso!" else "Erro ao cadastrar fornecedor.")
    }

    private fun listar() {
            println("--- Lista de Fornecedor ---")
            println("ID |   NOME  |  RAZAO  |  DOCUMENTO  | TELEFONE  |")
        repositorio.listarAtivos().filterIsInstance<Fornecedor>().forEach {
            println("${it.id} | ${it.nome}  |  ${it.razaoFornecedor}  |  ${it.documento} |  ${it.telefone}")
        }
        println("---------------------------")
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
