package caixadaagua

import enums.CorCaixa
import enums.Formato
import enums.MarcaCaixa
import enums.Material
import model.CaixaDagua
import model.Fornecedor
import pessoas.PessoaRepository
import utils.lerDoubleSeguro
import utils.lerInteiroSeguro

// Controller responsavel por exibir os menus e conversar com o usuario
// no modulo de Caixa d'Agua (estoque de produtos).
class CaixaDaAguaController {

    fun executar() {
        while (true) {
            println("\n--- Gerenciamento de Caixas d'Agua ---")
            println("1 - Visualizar Estoque")
            println("2 - Adicionar Caixa")
            println("3 - Editar Caixa")
            println("4 - Deletar Caixa")
            println("0 - Voltar")
            print("Opcao: ")

            when (readln()) {
                "1" -> visualizarEstoque()
                "2" -> adicionarCaixa()
                "3" -> editarCaixa()
                "4" -> deletarCaixa()
                "0" -> break
                else -> println("Opcao invalida.")
            }
        }
    }

    // Mostra todas as caixas d'agua cadastradas no banco.
    private fun visualizarEstoque() {
        val caixas = CaixaDaAguaRepository.listar()
        if (caixas.isEmpty()) {
            println("Estoque vazio.")
            return
        }

        println("--- ESTOQUE ---")
        println("ID |  MARCA |  MODELO  |  CAPACIDADE  |  QTD  |  PREÇO  |  FORNECEDOR ")
        caixas.forEach {
            println(
                "${it.id}  |  ${it.marca}|  ${it.modelo}  |  ${it.capacidadeLitros}  |  ${it.quantidade}  |  R$${it.preco}  |  ${it.nomeFornecedor}"
            )
        }
    }

    // Le do usuario os dados de uma nova caixa e manda salvar no banco.
    private fun adicionarCaixa() {
        val caixa = lerDadosCaixa() ?: return
        val id = CaixaDaAguaRepository.inserir(caixa)
        println("Caixa cadastrada com sucesso! ID: $id")
    }

    // Permite alterar uma caixa existente, buscando pelo ID.
    private fun editarCaixa() {
        val id = lerInteiroSeguro("ID da caixa") ?: return
        val caixaAtual = CaixaDaAguaRepository.buscarPorId(id)

        if (caixaAtual == null) {
            println("Caixa nao encontrada.")
            return
        }

        println("Deixe em branco para manter o valor atual.")

        val marca = lerMarcaOuPadrao(caixaAtual.marca)
        val fornecedorId = buscarFornecedorPorMarca(marca) ?: return

        val capacidade = lerInteiroOuPadrao("Capacidade (litros)", caixaAtual.capacidadeLitros)
        val modelo = "Caixa d' agua ${marca.name} ${capacidade}L"
        val largura = lerDoubleOuPadrao("Largura", caixaAtual.largura)
        val altura = lerDoubleOuPadrao("Altura", caixaAtual.altura)
        val profundidade = lerDoubleOuPadrao("Profundidade", caixaAtual.profundidade)
        val cor = lerCorOuPadrao(caixaAtual.cor)
        val material = lerMaterialOuPadrao(caixaAtual.material)
        val formato = lerFormatoOuPadrao(caixaAtual.formato)
        val preco = lerDoubleOuPadrao("Preco", caixaAtual.preco)
        val quantidade = lerInteiroOuPadrao("Quantidade", caixaAtual.quantidade)

        val caixaAtualizada =
            caixaAtual.copy(
                marca = marca,
                modelo = modelo,
                capacidadeLitros = capacidade,
                largura = largura,
                altura = altura,
                profundidade = profundidade,
                cor = cor,
                material = material,
                formato = formato,
                preco = preco,
                quantidade = quantidade,
                fornecedorId = fornecedorId,
            )

        val ok = CaixaDaAguaRepository.atualizar(caixaAtualizada)
        println(if (ok) "Caixa atualizada com sucesso!" else "Erro ao atualizar caixa.")
    }

    // Remove uma caixa do estoque pelo ID.
    private fun deletarCaixa() {
        val id = lerInteiroSeguro("ID da caixa") ?: return
        val ok = CaixaDaAguaRepository.deletar(id)
        println(if (ok) "Caixa removida com sucesso!" else "Caixa nao encontrada.")
    }

    // Le todos os dados de uma caixa d'agua nova.
    // Se o usuario digitar algo errado, retorna null e cancela a operacao.
    private fun lerDadosCaixa(): CaixaDagua? {
        println("Marca: 1-Fortlev 2-Aqualimp 3-Tigre")
        val marca =
            when (readln()) {
                "1" -> MarcaCaixa.Fortlev
                "2" -> MarcaCaixa.Aqualimp
                "3" -> MarcaCaixa.Tigre
                else -> {
                    println("Marca invalida.")
                    return null
                }
            }

        val fornecedorId = buscarFornecedorPorMarca(marca) ?: return null

        val capacidade = lerInteiroSeguro("Capacidade (litros)") ?: return null
        val modelo = "Caixa d' agua ${marca.name} ${capacidade}L"
        val largura = lerDoubleSeguro("Largura") ?: return null
        val altura = lerDoubleSeguro("Altura") ?: return null
        val profundidade = lerDoubleSeguro("Profundidade") ?: return null

        println("Cor: 1-Azul 2-Bege 3-Preta")
        val cor =
            when (readln()) {
                "1" -> CorCaixa.Azul
                "2" -> CorCaixa.Bege
                "3" -> CorCaixa.Preta
                else -> {
                    println("Cor invalida.")
                    return null
                }
            }

        println("Material: 1-POLIETILENO 2-FIBRA_DE_VIDRO 3-INOX")
        val material =
            when (readln()) {
                "1" -> Material.POLIETILENO
                "2" -> Material.FIBRA_DE_VIDRO
                "3" -> Material.INOX
                else -> {
                    println("Material invalido.")
                    return null
                }
            }

        println("Formato: 1-Redondo 2-Quadrado 3-Estreito 4-Conico")
        val formato =
            when (readln()) {
                "1" -> Formato.Redondo
                "2" -> Formato.Quadrado
                "3" -> Formato.Estreito
                "4" -> Formato.Conico
                else -> {
                    println("Formato invalido.")
                    return null
                }
            }

        val preco = lerDoubleSeguro("Preco") ?: return null
        val quantidade = lerInteiroSeguro("Quantidade em estoque") ?: return null

        return CaixaDagua(
            id = 0, // ID sera gerado pelo banco
            marca = marca,
            modelo = modelo,
            capacidadeLitros = capacidade,
            largura = largura,
            altura = altura,
            profundidade = profundidade,
            cor = cor,
            material = material,
            formato = formato,
            preco = preco,
            quantidade = quantidade,
            fornecedorId = fornecedorId,
        )
    }

    // Funcoes helper para editar: se o usuario apertar Enter, mantem o valor atual.
    private fun lerStringOuPadrao(label: String, padrao: String): String {
        print("$label [$padrao]: ")
        return readln().ifBlank { padrao }
    }

    private fun lerInteiroOuPadrao(label: String, padrao: Int): Int {
        print("$label [$padrao]: ")
        return readln().toIntOrNull() ?: padrao
    }

    private fun lerDoubleOuPadrao(label: String, padrao: Double): Double {
        print("$label [$padrao]: ")
        return readln().toDoubleOrNull() ?: padrao
    }

    private fun lerMaterialOuPadrao(padrao: Material): Material {
        println("Material [${padrao.name}]: 1-POLIETILENO 2-FIBRA_DE_VIDRO 3-INOX")
        return when (readln()) {
            "1" -> Material.POLIETILENO
            "2" -> Material.FIBRA_DE_VIDRO
            "3" -> Material.INOX
            "" -> padrao
            else -> {
                println("Material mantido como padrao.")
                padrao
            }
        }
    }

    private fun lerFormatoOuPadrao(padrao: Formato): Formato {
        println("Formato [${padrao.name}]: 1-Redondo 2-Quadrado 3-Estreito 4-Conico")
        return when (readln()) {
            "1" -> Formato.Redondo
            "2" -> Formato.Quadrado
            "3" -> Formato.Estreito
            "4" -> Formato.Conico
            "" -> padrao
            else -> {
                println("Formato mantido como padrao.")
                padrao
            }
        }
    }

    private fun lerCorOuPadrao(padrao: CorCaixa): CorCaixa {
        println("Cor [${padrao.name}]: 1-Azul 2-Bege 3-Preta")
        return when (readln()) {
            "1" -> CorCaixa.Azul
            "2" -> CorCaixa.Bege
            "3" -> CorCaixa.Preta
            "" -> padrao
            else -> {
                println("Cor mantida como padrao.")
                padrao
            }
        }
    }

    // Busca fornecedores ativos que vendam a marca escolhida.
    // Se houver so um, retorna o ID dele. Se houver varios, mostra para escolher.
    private fun buscarFornecedorPorMarca(marca: MarcaCaixa): Int? {
        val fornecedores =
            PessoaRepository.getInstancia().listarAtivos().filterIsInstance<Fornecedor>().filter {
                it.marca == marca
            }

        if (fornecedores.isEmpty()) {
            println("Nenhum fornecedor ativo encontrado para a marca $marca.")
            return null
        }

        if (fornecedores.size == 1) {
            val fornecedor = fornecedores.first()
            println("Fornecedor selecionado automaticamente: ${fornecedor.nome}")
            return fornecedor.id
        }

        println("Fornecedores disponiveis para a marca $marca:")
        fornecedores.forEach { println("${it.id} - ${it.nome}") }

        val id = lerInteiroSeguro("ID do fornecedor") ?: return null
        val escolhido = fornecedores.find { it.id == id }

        if (escolhido == null) {
            println("Fornecedor invalido.")
            return null
        }

        return escolhido.id
    }

    private fun lerMarcaOuPadrao(padrao: MarcaCaixa): MarcaCaixa {
        println("Marca [${padrao.name}]: 1-Fortlev 2-Aqualimp 3-Tigre")
        return when (readln()) {
            "1" -> MarcaCaixa.Fortlev
            "2" -> MarcaCaixa.Aqualimp
            "3" -> MarcaCaixa.Tigre
            "" -> padrao
            else -> {
                println("Marca mantida como padrao.")
                padrao
            }
        }
    }
}
