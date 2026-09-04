package pessoa

class PessoaController(private val repositorio: PessoaRepository) {

    fun executar() {
        while (true) {
            println("--- Gerenciamento de Pessoas ---")
            println("1 - Cadastrar Funcionario")
            println("2 - Cadastrar Fornecedor")
            println("3 - Cadastrar Cliente")
            println("4 - Listar Pessoas")
            println("5 - Inativar Pessoa")
            println("0 - Voltar")
            print("Opcao: ")

            when (readln()) {
                "1" -> cadastrarFuncionario()
                "2" -> cadastrarFornecedor()
                "3" -> cadastrarCliente()
                "4" -> listar()
                "5" -> inativar()
                "0" -> break
                else -> println("Opcao invalida.")
            }
        }
    }

    private fun cadastrarFuncionario() {
        print("Nome: ")
        val nome = readln()
        print("Documento: ")
        val documento = readln()
        print("Telefone: ")
        val telefone = readln()
        print("Email: ")
        val email = readln()
        println("Setor: 1-Financeiro 2-Comercial 3-Manutencao")
        val setor = when (readln()) {
            "1" -> Setor.FINANCEIRO
            "2" -> Setor.COMERCIAL
            "3" -> Setor.MANUTENCAO
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
        val funcionario = Funcionario(id, nome, documento, telefone, email, setor)
        repositorio.adicionar(funcionario)
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