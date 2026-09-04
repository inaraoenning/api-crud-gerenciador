package model

import enums.Formato
import enums.Material

data class CaixaDagua(
    val idCaixa: Int,
    val nomeCaixa: String,
    val modeloCaixa:String,
    val precoCaixa: Double,
    val largura: Double,
    val altura: Double,
    val profundidade: Double,
    val material: Material,
    val formato: Formato,
    val capacidadeLitros: Int,
    val quantidadeEstoque: Int,
    val fornecedorCaixa: Fornecedor
)