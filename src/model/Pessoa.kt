package model

open class Pessoa(
    val id: Int,
    val nome: String,
    val documento: String,
    val telefone: String,
    val tipo: TipoPessoa,
    var ativo: Boolean = true
)