package utils

// Objeto que centraliza as validacoes do sistema.
// As regex garantem que os dados digitados pelo usuario estejam no formato correto
// antes de chegarem ao banco.
object Validador {
    // Regex para CPF (11 digitos) ou CNPJ (14 digitos).
    // ^ inicio da string, \d qualquer digito, {11} exatamente 11 vezes, | ou, {14} exatamente 14
    // vezes, $ fim da string.
    private val regexCpfCnpj = Regex("^\\d{11}$|^\\d{14}$")

    // Regex para telefone.
    // ^\(? permite DDD com parentese opcional, \d{2} dois digitos, \)? fecha parentese opcional,
    // \s? espaco opcional, \d{4,5} quatro ou cinco digitos, -? hifen opcional, \d{4} quatro digitos
    // finais.
    private val regexTelefone = Regex("^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$")

    // O parametro documento eh nullable (String?) porque pode vir nulo em alguma chamada.
    // Safe call (?.) so executa matches se documento nao for nulo.
    // Elvis operator (?:) retorna false caso documento seja nulo.
    fun documentoValido(documento: String?): Boolean {
        return documento?.matches(regexCpfCnpj) ?: false
    }

    // Mesma ideia do documento: telefone pode ser nulo e a validacao usa safe call + elvis.
    fun telefoneValido(telefone: String?): Boolean {
        return telefone?.matches(regexTelefone) ?: false
    }

    // Valida razao social com regex que permite letras, acentos, numeros, espacos e alguns
    // simbolos.
    // isNotBlank() impede que o usuario envie apenas espacos em branco.
    fun razaoSocialValida(razaoSocial: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9À-ÿ'\\s.\\-/&]+$")
        return razaoSocial.isNotBlank() && regex.matches(razaoSocial)
    }
}

// Le um numero inteiro de forma segura.
// O retorno eh Int? (nullable) porque o usuario pode digitar algo invalido.
// Bloco try/catch captura NumberFormatException e evita que o programa quebre.
fun lerInteiroSeguro(label: String): Int? {
    print("$label: ")
    return try {
        readln().toIntOrNull()
    } catch (e: NumberFormatException) {
        println("Numero invalido.")
        null
    }
}

// Le um numero decimal de forma segura.
// Usa while para ficar perguntando ate o usuario digitar um valor valido e positivo.
fun lerDoubleSeguro(label: String): Double {
    while (true) {
        print("$label: ")
        val valor = readln().toDoubleOrNull()
        if (valor != null && valor > 0) {
            return valor
        }
        println("Valor invalido.")
    }
}
