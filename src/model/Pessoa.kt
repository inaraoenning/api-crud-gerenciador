package model

// Classe base (superclasse) para todas as pessoas do sistema.
// Usamos 'open' para permitir que Funcionario, Cliente e Fornecedor herdem dela.
// A heranca evita repetir campos comuns (id, nome, documento, telefone) em cada classe filha.
open class Pessoa(
    val id: Int,
    val nome: String,
    val documento: String,
    val telefone: String,
    val tipo: TipoPessoa,
    var ativo: Boolean = true,
)
