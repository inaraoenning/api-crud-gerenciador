package pessoas.funcionario

import enums.Setor
import lerInteiroSeguro
import model.Funcionario
import pessoas.pessoa.PessoaRepository

class FuncionarioController(private val repositorio: PessoaRepository) {

    private fun cadastrarFuncionario() {
        print("Nome: ")
        val nome = readln()
        print("Documento: ")
        val documento = readln()
        print("Telefone: ")
        val telefone = readln()
        println("Setor: 1-Financeiro 2-Comercial 3-Manutencao")
        val setor = when (readln()) {
            "1" -> Setor.entries.equals("FINANCEIRO")
            "2" -> Setor.entries.equals("COMERCIAL")
            "3" -> Setor.entries.equals("MANUTENCAO")
            else -> {
                println("Setor invalido.")
                return
            }
        }

        if (!Validador.documentoValido(documento)) {
            println("Documento invalido.")
            return
        }

        val id = repositorio.listar().size + 1
        val funcionario = Funcionario(id, nome, documento, telefone, setor)
        // repositorio.adicionar(funcionario)
        println("Funcionario cadastrado com sucesso!")
    }

    private fun listar() {
        repositorio.listarAtivos().forEach {
            println("${it.id} -${it.nome} [${it.tipo}]")
        }
    }

    private fun inativar() {
        val id = lerInteiroSeguro("ID da pessoa") ?: return
        val ok = repositorio.inativar(id)
        println(if (ok) "Inativado." else "Pessoa nao encontrada.")
    }
}