package venda

import caixadaagua.CaixaDaAguaRepository
import financeiro.FinanceiroRepository
import lerInteiroSeguro
import model.Cliente
import model.Funcionario
import model.ItemVenda
import model.MovimentacaoFinanceira
import model.TipoMovimentacao
import model.Venda
import pessoas.pessoa.PessoaRepository
import utils.Logger

// Controller da frente de caixa.
// Permite criar vendas, ver o historico e estornar vendas.
class VendaController {

    fun executar() {
        while (true) {
            println("\n--- Frente de Caixa ---")
            println("1 - Realizar Venda")
            println("2 - Estornar Venda")
            println("3 - Historico de Vendas")
            println("0 - Voltar")
            print("Opcao: ")

            when (readln()) {
                "1" -> realizarVenda()
                "2" -> estornarVenda()
                "3" -> historicoVendas()
                "0" -> break
                else -> println("Opcao invalida.")
            }
        }
    }

    // Menu para montar uma venda: escolhe funcionario, cliente e itens.
    private fun realizarVenda() {
        println("Funcionarios disponiveis:")
        val funcionarios = PessoaRepository.getInstancia().listarAtivos().filterIsInstance<Funcionario>()
        if (funcionarios.isEmpty()) {
            println("Nenhum funcionario cadastrado. Cadastre um funcionario antes de vender.")
            return
        }
        funcionarios.forEach { println("${it.id} - ${it.nome}") }
        val funcionarioId = lerInteiroSeguro("ID do funcionario") ?: return
        if (funcionarios.none { it.id == funcionarioId }) {
            println("Funcionario invalido.")
            return
        }

        println("Clientes disponiveis:")
        val clientes = PessoaRepository.getInstancia().listarAtivos().filterIsInstance<Cliente>()
        if (clientes.isEmpty()) {
            println("Nenhum cliente cadastrado. Cadastre um cliente antes de vender.")
            return
        }
        clientes.forEach { println("${it.id} - ${it.nome}") }
        val clienteId = lerInteiroSeguro("ID do cliente") ?: return
        if (clientes.none { it.id == clienteId }) {
            println("Cliente invalido.")
            return
        }

        val itens = montarItens()
        if (itens.isEmpty()) {
            println("Venda cancelada: nenhum item adicionado.")
            return
        }

        val total = itens.sumOf { it.valorTotal }
        val venda = Venda(
            funcionarioId = funcionarioId,
            clienteId = clienteId,
            valorTotal = total,
            itens = itens
        )

        try {
            val vendaId = VendaRepository.inserir(venda)

            // Registra a venda como entrada financeira.
            FinanceiroRepository.registrar(
                MovimentacaoFinanceira(
                    valor = total,
                    pagador = clientes.first { it.id == clienteId }.nome,
                    recebedor = "Empresa",
                    motivo = "Venda ID $vendaId",
                    responsavel = funcionarios.first { it.id == funcionarioId }.nome,
                    tipo = TipoMovimentacao.ENTRADA
                )
            )

            println("Venda realizada com sucesso! ID: $vendaId | Total: R$ $total")
            Logger.info("Venda ID $vendaId realizada no valor de R$ $total")
        } catch (e: Exception) {
            println("Erro ao registrar venda: ${e.message}")
            Logger.erro("Erro ao registrar venda: ${e.message}")
        }
    }

    // Loop para adicionar varios itens na venda ate o usuario escolher finalizar.
    private fun montarItens(): List<ItemVenda> {
        val itens = mutableListOf<ItemVenda>()

        while (true) {
            println("\nAdicionar item:")
            println("1 - Caixa d'Agua")
            println("2 - Servico")
            println("0 - Finalizar")
            print("Opcao: ")

            when (readln()) {
                "1" -> adicionarItemCaixa(itens)
                "2" -> adicionarItemServico(itens)
                "0" -> break
                else -> println("Opcao invalida.")
            }
        }

        return itens
    }

    // Adiciona um produto (caixa d'agua) na venda.
    private fun adicionarItemCaixa(itens: MutableList<ItemVenda>) {
        val caixas = CaixaDaAguaRepository.listar()
        if (caixas.isEmpty()) {
            println("Nenhuma caixa cadastrada em estoque.")
            return
        }

        caixas.forEach {
            println("${it.id} - ${it.nome} | Preco: R$ ${it.preco} | Estoque: ${it.quantidade}")
        }

        val id = lerInteiroSeguro("ID da caixa") ?: return
        val caixa = caixas.find { it.id == id }
        if (caixa == null) {
            println("Caixa nao encontrada.")
            return
        }

        val qtd = lerInteiroSeguro("Quantidade") ?: return
        if (qtd <= 0 || qtd > caixa.quantidade) {
            println("Quantidade invalida. Estoque disponivel: ${caixa.quantidade}")
            return
        }

        itens.add(
            ItemVenda(
                caixaDaguaId = caixa.id,
                quantidade = qtd,
                precoUnitario = caixa.preco,
                valorTotal = qtd * caixa.preco
            )
        )
        println("Item adicionado: ${caixa.nome} x$qtd")
    }

    // Adiciona um servico na venda.
    private fun adicionarItemServico(itens: MutableList<ItemVenda>) {
        val servicos = servico.ServicoRepository.listar()
        if (servicos.isEmpty()) {
            println("Nenhum servico cadastrado.")
            return
        }

        servicos.forEach {
            println("${it.id} - ${it.nome} | Preco: R$ ${it.preco}")
        }

        val id = lerInteiroSeguro("ID do servico") ?: return
        val servico = servicos.find { it.id == id }
        if (servico == null) {
            println("Servico nao encontrado.")
            return
        }

        val qtd = lerInteiroSeguro("Quantidade") ?: return
        if (qtd <= 0) {
            println("Quantidade invalida.")
            return
        }

        itens.add(
            ItemVenda(
                servicoId = servico.id,
                quantidade = qtd,
                precoUnitario = servico.preco,
                valorTotal = qtd * servico.preco
            )
        )
        println("Item adicionado: ${servico.nome} x$qtd")
    }

    // Estorna uma venda pelo ID (remove do banco e devolve estoque).
    private fun estornarVenda() {
        val id = lerInteiroSeguro("ID da venda") ?: return
        val venda = VendaRepository.buscarPorId(id)

        if (venda == null) {
            println("Venda nao encontrada.")
            return
        }

        print("Confirma estorno da venda $id no valor de R$ ${venda.valorTotal}? (s/n): ")
        if (readln().lowercase() != "s") {
            println("Estorno cancelado.")
            return
        }

        try {
            val ok = VendaRepository.estornar(id)
            if (ok) {
                // Registra uma saida financeira para compensar o estorno.
                FinanceiroRepository.registrar(
                    MovimentacaoFinanceira(
                        valor = venda.valorTotal,
                        pagador = "Empresa",
                        recebedor = "Cliente",
                        motivo = "Estorno da venda ID $id",
                        responsavel = "Sistema",
                        tipo = TipoMovimentacao.SAIDA
                    )
                )
                println("Venda estornada com sucesso!")
                Logger.info("Venda ID $id estornada no valor de R$ ${venda.valorTotal}")
            } else {
                println("Nao foi possivel estornar a venda.")
            }
        } catch (e: Exception) {
            println("Erro ao estornar venda: ${e.message}")
            Logger.erro("Erro ao estornar venda ID $id: ${e.message}")
        }
    }

    // Exibe todas as vendas cadastradas, com opcao de ver detalhes.
    private fun historicoVendas() {
        val vendas = VendaRepository.listar()
        if (vendas.isEmpty()) {
            println("Nenhuma venda registrada.")
            return
        }

        vendas.forEach {
            println("ID: ${it.id} | Funcionario: ${it.funcionarioId} | Cliente: ${it.clienteId} | " +
                    "Data: ${it.dataHora} | Total: R$ ${it.valorTotal}")
        }

        print("Digite o ID para ver detalhes (ou Enter para voltar): ")
        val id = readln().toIntOrNull()
        if (id != null) {
            val venda = VendaRepository.buscarPorId(id)
            if (venda != null) {
                println("\nDetalhes da venda ${venda.id}:")
                venda.itens.forEach { item ->
                    val tipo = when {
                        item.caixaDaguaId != null -> "Produto ID ${item.caixaDaguaId}"
                        item.servicoId != null -> "Servico ID ${item.servicoId}"
                        else -> "Item desconhecido"
                    }
                    println("  $tipo | Qtd: ${item.quantidade} | Unit: R$ ${item.precoUnitario} | Total: R$ ${item.valorTotal}")
                }
            } else {
                println("Venda nao encontrada.")
            }
        }
    }
}
