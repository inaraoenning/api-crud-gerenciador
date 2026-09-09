package model

// Cliente herda de Pessoa.
// Nao adiciona campos novos, so define o tipo como CLIENTE para o sistema diferenciar.
class Cliente(
    idCliente: Int,
    nomeCliente: String,
    documentoCliente: String,
    telefoneCliente: String,
) :
    Pessoa(
        id = idCliente,
        nome = nomeCliente,
        documento = documentoCliente,
        telefone = telefoneCliente,
        tipo = TipoPessoa.CLIENTE,
    )
