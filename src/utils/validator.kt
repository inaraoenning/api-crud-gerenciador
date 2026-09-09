package utils

object Validador {
    private val regexCpfCnpj = Regex("^\\d{11}$|^\\d{14}$")
    private val regexTelefone = Regex("^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$")

    fun documentoValido(documento: String?): Boolean {
        return documento?.matches(regexCpfCnpj) ?: false
    }

    fun telefoneValido(telefone: String?): Boolean {
        return telefone?.matches(regexTelefone) ?: false
    }

    fun razaoSocialValida(razaoSocial: String): Boolean {
        // Permite letras (com e sem acento), números, espaços, apóstrofo ('), hífen (-), ponto (.)
        // e comercial (&)
        val regex = Regex("^[a-zA-Z0-9À-ÿ'\\s.\\-/&]+$")
        return razaoSocial.isNotBlank() && regex.matches(razaoSocial)
    }
}

fun lerInteiroSeguro(label: String): Int? {
    print("$label: ")
    return try {
        readln().toIntOrNull()
    } catch (e: NumberFormatException) {
        println("Numero invalido.")
        null
    }
}

// Valida se o valor é nulo, não aceita valor negativo e usa while para continuar na função até
// inserir o valor correto
fun lerDoubleSeguro(label: String): Double? {
    while (true) {
        print("$label: ")
        val valor = readln().toDoubleOrNull()
        if (valor != null && valor > 0) {
            return valor
        }
        println("Valor invalido.")
    }
}
