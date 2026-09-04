package model

data class Fornecedor(
    val idFornecedor: Int,
    val nomeFornecedor: String,
    val documentoFornecedor: String,
    val telefoneFornecedor: String,
) : Pessoa(
    id = idFornecedor,
    nome = nomeFornecedor,
    documento = documentoFornecedor,
    telefone = telefoneFornecedor,
    tipo = TipoPessoa.FORNECEDOR
)