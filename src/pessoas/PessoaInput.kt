package pessoas

import utils.Validador

// Classe simples para agrupar os dados comuns de toda pessoa.
// Os campos sao nao nullable (String, nao String?) porque aqui ja exigimos preenchimento.
data class DadosComunsPessoa(val nome: String, val documento: String, val telefone: String)

// Le nome, documento e telefone, validando cada campo antes de devolver.
// Usa while para repetir a pergunta ate o usuario acertar.
fun lerDadosComunsPessoa(repositorio: PessoaRepository): DadosComunsPessoa {
    print("Nome: ")
    // readln() pode retornar string vazia; ainda nao bloqueia nome em branco.
    val nome = readln()

    var documento: String
    while (true) {
        print("Documento (CPF/CNPJ): ")
        documento = readln()

        // documentoValido recebe String? (nullable), mas aqui eh String.
        // Nao usamos !! porque a funcao aceita nullable diretamente.
        if (!Validador.documentoValido(documento)) {
            println(
                "Documento invalido. Use 11 digitos (CPF) ou 14 digitos (CNPJ). Tente novamente."
            )
            continue
        }

        // Verifica duplicidade no banco antes de continuar.
        if (repositorio.documentoJaExiste(documento)) {
            println("Documento ja cadastrado. Informe um documento diferente.")
            continue
        }

        break
    }

    var telefone: String
    while (true) {
        print("Telefone: ")
        telefone = readln()

        if (Validador.telefoneValido(telefone)) {
            break
        }
        println("Telefone invalido. Tente novamente.")
    }

    return DadosComunsPessoa(nome, documento, telefone)
}
