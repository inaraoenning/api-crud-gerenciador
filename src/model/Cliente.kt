package model

class Cliente(
    idCliente: Int,
    nomeCliente: String,
    documentoCliente: String,
    telefoneCliente: String
) : Pessoa(
    id = idCliente,
    nome = nomeCliente,
    documento = documentoCliente,
    telefone = telefoneCliente,
    tipo = TipoPessoa.CLIENTE
)