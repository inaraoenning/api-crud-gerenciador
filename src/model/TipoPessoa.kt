package model

// Enum que identifica se uma pessoa eh funcionario, cliente ou fornecedor.
// Usado no mapeamento do banco para decidir qual objeto especifico criar (polimorfismo).
enum class TipoPessoa {
    FUNCIONARIO,
    FORNECEDOR,
    CLIENTE,
}
