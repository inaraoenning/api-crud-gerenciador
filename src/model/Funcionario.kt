package model

import enums.Setor
import java.math.BigDecimal

class Funcionario(
    idFuncionario: Int,
    nomeFuncionario: String,
    documentoFuncionario: String,
    telefoneFuncionario: String,
    val salario: BigDecimal,
    val setor: Setor
): Pessoa(
    id = idFuncionario,
    nome = nomeFuncionario,
    documento = documentoFuncionario,
    telefone = telefoneFuncionario,
    tipo = TipoPessoa.FUNCIONARIO)
