object Validador {
    private val regexCpfCnpj = Regex("^\\d{11}$|^\\d{14}$")
    private val regexTelefone = Regex("^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$")
    private val regexEmail = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun documentoValido(documento: String?): Boolean {
        return documento?.matches(regexCpfCnpj) ?: false
    }

    fun telefoneValido(telefone: String?): Boolean {
        return telefone?.matches(regexTelefone) ?: false
    }

    fun emailValido(email: String?): Boolean {
        return email?.matches(regexEmail) ?: false
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