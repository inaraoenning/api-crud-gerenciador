package model

import java.time.LocalDateTime

// Representa uma venda completa feita na frente de caixa.
// Uma venda tem um funcionario, um cliente, data/hora e uma lista de itens.
// O valor total e calculado somando os itens.
data class Venda(
    val id: Int = 0,
    val funcionarioId: Int,
    val clienteId: Int,
    val dataHora: LocalDateTime = LocalDateTime.now(),
    val valorTotal: Double,
    val itens: List<ItemVenda> = emptyList()
)
