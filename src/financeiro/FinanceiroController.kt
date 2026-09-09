package financeiro

import model.Funcionario
import model.MovimentacaoFinanceira
import model.TipoMovimentacao
import pessoas.PessoaRepository
import utils.Logger
import utils.lerDoubleSeguro
import utils.lerInteiroSeguro

// Controller do modulo financeiro.
// Aqui ficam as telas para pagamento de salarios, registro de compras/despesas
// e a consulta do saldo do caixa.
class FinanceiroController {

    fun executar() {
        while (true) {
            println("\n--- Modulo Financeiro ---")
            println("1 - Pagar Salario")
            println("2 - Registrar Compra/Despesa")
            println("3 - Registrar Entrada Manual")
            println("4 - Historico de Movimentacoes")
            println("5 - Saldo do Caixa")
            println("0 - Voltar")
            print("Opcao: ")

            when (readln()) {
                "1" -> pagarSalario()
                "2" -> registrarDespesa()
                "3" -> registrarEntrada()
                "4" -> historicoMovimentacoes()
                "5" -> saldoCaixa()
                "0" -> break
                else -> println("Opcao invalida.")
            }
        }
    }

    // Paga o salario de um funcionario, registrando como saida no caixa.
    private fun pagarSalario() {
        val funcionarios =
            PessoaRepository.getInstancia().listarAtivos().filterIsInstance<Funcionario>()
        if (funcionarios.isEmpty()) {
            println("Nenhum funcionario cadastrado.")
            return
        }

        funcionarios.forEach {
            println("${it.id} - ${it.nome} | Setor: ${it.setor} | Salario: R$ ${it.salario}")
        }
        val id = lerInteiroSeguro("ID do funcionario") ?: return
        val funcionario = funcionarios.find { it.id == id }

        if (funcionario == null) {
            println("Funcionario nao encontrado.")
            return
        }

        if (!FinanceiroRepository.temSaldo(funcionario.salario.toDouble())) {
            println("Saldo insuficiente para pagar o salario de R$ ${funcionario.salario}.")
            return
        }

        val movimentacao =
            MovimentacaoFinanceira(
                valor = funcionario.salario.toDouble(),
                pagador = "Empresa",
                recebedor = funcionario.nome,
                motivo = "Pagamento de salario - ${funcionario.nome}",
                responsavel = "Setor Financeiro",
                tipo = TipoMovimentacao.SAIDA,
            )

        try {
            val idMov = FinanceiroRepository.registrar(movimentacao)
            println("Salario pago! Movimentacao registrada ID: $idMov")
            Logger.info(
                "Salario pago ao funcionario ${funcionario.nome}: R$ ${funcionario.salario}"
            )
        } catch (e: Exception) {
            println("Erro ao pagar salario: ${e.message}")
            Logger.erro("Erro ao pagar salario de ${funcionario.nome}: ${e.message}")
        }
    }

    // Registra uma despesa/compra, como compra de mercadorias para o estoque.
    private fun registrarDespesa() {
        print("Descricao da despesa/compra: ")
        val motivo = readln()
        print("Pagador (quem pagou): ")
        val pagador = readln()
        print("Recebedor (quem recebeu): ")
        val recebedor = readln()
        print("Responsavel: ")
        val responsavel = readln()
        val valor = lerDoubleSeguro("Valor")

        val movimentacao =
            MovimentacaoFinanceira(
                valor = valor,
                pagador = pagador,
                recebedor = recebedor,
                motivo = motivo,
                responsavel = responsavel,
                tipo = TipoMovimentacao.SAIDA,
            )

        try {
            val id = FinanceiroRepository.registrar(movimentacao)
            println("Despesa registrada! ID: $id")
            Logger.info("Despesa registrada: $motivo - R$ $valor")
        } catch (e: Exception) {
            println("Erro ao registrar despesa: ${e.message}")
            Logger.erro("Erro ao registrar despesa: ${e.message}")
        }
    }

    // Registra uma entrada manual de dinheiro no caixa.
    private fun registrarEntrada() {
        print("Motivo da entrada: ")
        val motivo = readln()
        print("Pagador (quem pagou): ")
        val pagador = readln()
        print("Recebedor (quem recebeu): ")
        val recebedor = readln()
        print("Responsavel: ")
        val responsavel = readln()
        val valor = lerDoubleSeguro("Valor")

        val movimentacao =
            MovimentacaoFinanceira(
                valor = valor,
                pagador = pagador,
                recebedor = recebedor,
                motivo = motivo,
                responsavel = responsavel,
                tipo = TipoMovimentacao.ENTRADA,
            )

        try {
            val id = FinanceiroRepository.registrar(movimentacao)
            println("Entrada registrada! ID: $id")
            Logger.info("Entrada registrada: $motivo - R$ $valor")
        } catch (e: Exception) {
            println("Erro ao registrar entrada: ${e.message}")
            Logger.erro("Erro ao registrar entrada: ${e.message}")
        }
    }

    // Mostra todas as movimentacoes financeiras.
    private fun historicoMovimentacoes() {
        val movimentacoes = FinanceiroRepository.listar()
        if (movimentacoes.isEmpty()) {
            println("Nenhuma movimentacao registrada.")
            return
        }

        movimentacoes.forEach {
            println(
                "ID: ${it.id} | ${it.tipo} | R$ ${it.valor} | " +
                    "${it.motivo} | Pagador: ${it.pagador} | Recebedor: ${it.recebedor} | " +
                    "Resp: ${it.responsavel} | ${it.dataHora}"
            )
        }
    }

    // Exibe o saldo atual do caixa.
    private fun saldoCaixa() {
        val saldo = FinanceiroRepository.calcularSaldo()
        println("Saldo atual do caixa: R$ $saldo")
    }
}
