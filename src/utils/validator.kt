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
        // Permite letras (com e sem acento), números, espaços, apóstrofo ('), hífen (-), ponto (.) e comercial (&)
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

fun lerDoubleSeguro(label: String): Double? {
    print("$label: ")
    return try {
        readln().toDoubleOrNull()
    } catch (e: NumberFormatException) {
        println("Valor invalido.")
        null
    }
}