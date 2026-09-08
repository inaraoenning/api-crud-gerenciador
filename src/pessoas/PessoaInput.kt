package pessoas

data class DadosComunsPessoa(
    val nome: String,
    val documento: String,
    val telefone: String
)

fun lerDadosComunsPessoa(): DadosComunsPessoa? {
    print("Nome: ")
    val nome = readln()
    print("Documento (CPF/CNPJ): ")
    val documento = readln()
    print("Telefone: ")
    val telefone = readln()

    if (!Validador.documentoValido(documento)) {
        println("Documento invalido. Use 11 digitos (CPF) ou 14 digitos (CNPJ).")
        return null
    }

    if (!Validador.telefoneValido(telefone)) {
        println("Telefone invalido.")
        return null
    }

    return DadosComunsPessoa(nome, documento, telefone)
}
