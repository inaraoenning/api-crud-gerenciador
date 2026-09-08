package model

import enums.CorCaixa
import enums.Formato
import enums.Material

// Classe que representa uma caixa d'agua no estoque.
// batem com as colunas do banco de dados.
data class CaixaDagua(
    val id: Int,
    val marca: String,
    val modelo: String,
    val capacidadeLitros: Int,
    val largura: Double,
    val altura: Double,
    val profundidade: Double,
    val cor: CorCaixa,
    val material: Material,
    val formato: Formato,
    val preco: Double,
    val quantidade: Int,
    val fornecedorId: Int,
    val nomeFornecedor: String = "",
)
