package model

// Representa um servico oferecido pela loja, como manutencao ou instalacao.
// Bate com a tabela SERVICO do banco de dados.
data class Servico(
    val id: Int,
    val nome: String,
    val descricao: String,
    val preco: Double
)
