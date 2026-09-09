package model

import enums.MarcaCaixa

data class Fornecedor(
    val idFornecedor: Int,
    val nomeFornecedor: String,
    val razaoFornecedor: String,
    val documentoFornecedor: String,
    val telefoneFornecedor: String,
    val marca: MarcaCaixa,
) :
    Pessoa(
        id = idFornecedor,
        nome = nomeFornecedor,
        documento = documentoFornecedor,
        telefone = telefoneFornecedor,
        tipo = TipoPessoa.FORNECEDOR,
    )
