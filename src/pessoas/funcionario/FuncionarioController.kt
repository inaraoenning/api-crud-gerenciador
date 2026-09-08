package pessoas.funcionario

import enums.Setor
import java.math.BigDecimal
import utils.lerDoubleSeguro
import model.Funcionario
import pessoas.PessoaRepository
import pessoas.lerDadosComunsPessoa

class FuncionarioController(private val repositorio: PessoaRepository) {

    fun executar() {
        while (true) {
            println("--- Gerenciamento de Funcionarios ---")
            println("1 - Cadastrar Funcionario")
            println("2 - Listar Funcionarios")
            println("3 - Inativar Funcionario")
            println("0 - Voltar")
            print("Opcao: ")

            when (readln()) {
                "1" -> cadastrarFuncionario()
                "2" -> listar()
                "3" -> inativar()
                "0" -> break
                else -> println("Opcao invalida.")
            }
        }
    }

    private fun cadastrarFuncionario() {
        val dados = lerDadosComunsPessoa() ?: return

        println("Setor: 1-Financeiro 2-Comercial 3-Manutencao")
        val setor =
            when (readln()) {
                "1" -> Setor.FINANCEIRO
                "2" -> Setor.COMERCIAL
                "3" -> Setor.MANUTENCAO
                else -> {
                    println("Setor invalido.")
                    return
                }
            }

        val salario = lerDoubleSeguro("Salario") ?: return

        val funcionario =
            Funcionario(
                idFuncionario = 0,
                nomeFuncionario = dados.nome,
                documentoFuncionario = dados.documento,
                telefoneFuncionario = dados.telefone,
                salario = BigDecimal.valueOf(salario),
                setor = setor,
            )

        val ok = repositorio.inserir(funcionario, salario = salario, setor = setor.name)
        println(if (ok) "Funcionario cadastrado com sucesso!" else "Erro ao cadastrar funcionario.")
    }

    private fun listar() {
        repositorio.listarAtivos().filterIsInstance<Funcionario>().forEach {
            println("${it.id} - ${it.nome} | Setor: ${it.setor} | Salario: R$ ${it.salario}")
        }
    }

    private fun inativar() {
        print("ID do funcionario: ")
        val id = readln().toIntOrNull()
        if (id == null) {
            println("ID invalido.")
            return
        }
        val ok = repositorio.inativar(id)
        println(if (ok) "Funcionario inativado." else "Funcionario nao encontrado.")
    }
}
