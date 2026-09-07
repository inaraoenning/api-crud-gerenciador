package model

import java.time.LocalDateTime

// Representa uma entrada ou saida de dinheiro do caixa.
// Bate com a tabela MOVIMENTACAO_FINANCEIRA do banco.
data class MovimentacaoFinanceira(
    val id: Int = 0,
    val valor: Double,
    val pagador: String,
    val recebedor: String,
    val dataHora: LocalDateTime = LocalDateTime.now(),
    val motivo: String,
    val responsavel: String,
    val tipo: TipoMovimentacao
)
