package model

import enums.Setor
import java.math.BigDecimal

// Funcionario herda de Pessoa e adiciona salario e setor.
// Heranca eh usada porque funcionario, cliente e fornecedor compartilham os mesmos dados basicos.
class Funcionario(
    idFuncionario: Int,
    nomeFuncionario: String,
    documentoFuncionario: String,
    telefoneFuncionario: String,
    val salario: BigDecimal,
    val setor: Setor,
) :
    Pessoa(
        id = idFuncionario,
        nome = nomeFuncionario,
        documento = documentoFuncionario,
        telefone = telefoneFuncionario,
        tipo = TipoPessoa.FUNCIONARIO,
    )
