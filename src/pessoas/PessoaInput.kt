package pessoas

import utils.Validador

data class DadosComunsPessoa(val nome: String, val documento: String, val telefone: String)

fun lerDadosComunsPessoa(): DadosComunsPessoa {
    print("Nome: ")
    val nome = readln()

    var documento: String
    while (true) {
        print("Documento (CPF/CNPJ): ")
        documento = readln()

        if (Validador.documentoValido(documento)) {
            break
        }
        println("Documento inválido. Use 11 dígitos (CPF) ou 14 dígitos (CNPJ). Tente novamente.")
    }

    var telefone: String
    while (true) {
        print("Telefone: ")
        telefone = readln()

        if (Validador.telefoneValido(telefone)) {
            break
        }
        println("Telefone inválido. Tente novamente.")
    }

    return DadosComunsPessoa(nome, documento, telefone)
}
