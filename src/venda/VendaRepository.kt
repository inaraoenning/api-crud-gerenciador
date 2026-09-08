package venda

import database.DbConnection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import model.ItemVenda
import model.Venda

// Repository responsavel pelas vendas.
// Uma venda envolve duas tabelas: VENDA (cabecalho) e VENDA_ITEM (itens).
// Por isso, todas as operacoes usam transacao para garantir consistencia.
object VendaRepository {

    private fun mapearVenda(rs: ResultSet): Venda {
        return Venda(
            id = rs.getInt("id"),
            funcionarioId = rs.getInt("funcionario_id"),
            clienteId = rs.getInt("cliente_id"),
            dataHora = rs.getTimestamp("data_hora").toLocalDateTime(),
            valorTotal = rs.getDouble("valor_total"),
        )
    }

    private fun mapearItem(rs: ResultSet): ItemVenda {
        return ItemVenda(
            id = rs.getInt("id"),
            vendaId = rs.getInt("venda_id"),
            // Converte Integer (Java) para Int? de forma segura.
            caixaDaguaId = rs.getObject("caixa_da_agua_id") as? Int,
            servicoId = rs.getObject("servico_id") as? Int,
            quantidade = rs.getInt("quantidade"),
            precoUnitario = rs.getDouble("preco_unitario"),
            valorTotal = rs.getDouble("valor_total"),
        )
    }

    // Cria uma venda completa com seus itens, atualiza o estoque e retorna o ID da venda.
    fun inserir(venda: Venda): Int {
        val sqlVenda =
            """
            INSERT INTO VENDA (funcionario_id, cliente_id, data_hora, valor_total)
            VALUES (?, ?, ?, ?)
            """
                .trimIndent()

        val sqlItem =
            """
            INSERT INTO VENDA_ITEM (venda_id, caixa_da_agua_id, servico_id, quantidade, preco_unitario, valor_total)
            VALUES (?, ?, ?, ?, ?, ?)
            """
                .trimIndent()

        val sqlAtualizaEstoque = "UPDATE CAIXA_DA_AGUA SET quantidade = quantidade - ? WHERE id = ?"

        DbConnection.conectar().use { conn ->
            conn.autoCommit = false

            try {
                val vendaId =
                    conn.prepareStatement(sqlVenda, PreparedStatement.RETURN_GENERATED_KEYS).use {
                        stmt ->
                        stmt.setInt(1, venda.funcionarioId)
                        stmt.setInt(2, venda.clienteId)
                        stmt.setTimestamp(3, Timestamp.valueOf(venda.dataHora))
                        stmt.setDouble(4, venda.valorTotal)
                        stmt.executeUpdate()

                        val rs = stmt.generatedKeys
                        rs.next()
                        rs.getInt(1)
                    }

                conn.prepareStatement(sqlItem).use { stmt ->
                    for (item in venda.itens) {
                        stmt.setInt(1, vendaId)
                        stmt.setObject(2, item.caixaDaguaId)
                        stmt.setObject(3, item.servicoId)
                        stmt.setInt(4, item.quantidade)
                        stmt.setDouble(5, item.precoUnitario)
                        stmt.setDouble(6, item.valorTotal)
                        stmt.addBatch()

                        // Se o item for um produto, diminui do estoque.
                        if (item.caixaDaguaId != null) {
                            conn.prepareStatement(sqlAtualizaEstoque).use { estoqueStmt ->
                                estoqueStmt.setInt(1, item.quantidade)
                                estoqueStmt.setInt(2, item.caixaDaguaId)
                                estoqueStmt.executeUpdate()
                            }
                        }
                    }
                    stmt.executeBatch()
                }

                conn.commit()
                return vendaId
            } catch (e: Exception) {
                conn.rollback()
                throw e
            } finally {
                conn.autoCommit = true
            }
        }
    }

    // Lista todas as vendas cadastradas (sem itens).
    fun listar(): List<Venda> {
        val sql = "SELECT * FROM VENDA ORDER BY data_hora DESC"
        val vendas = mutableListOf<Venda>()

        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    vendas.add(mapearVenda(rs))
                }
            }
        }

        return vendas
    }

    // Busca uma venda pelo ID, ja trazendo seus itens junto.
    fun buscarPorId(id: Int): Venda? {
        val sqlVenda = "SELECT * FROM VENDA WHERE id = ?"
        val sqlItens = "SELECT * FROM VENDA_ITEM WHERE venda_id = ?"

        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sqlVenda).use { stmt ->
                stmt.setInt(1, id)
                val rs = stmt.executeQuery()
                if (!rs.next()) return null

                val venda = mapearVenda(rs)
                val itens = mutableListOf<ItemVenda>()

                conn.prepareStatement(sqlItens).use { itemStmt ->
                    itemStmt.setInt(1, id)
                    val rsItens = itemStmt.executeQuery()
                    while (rsItens.next()) {
                        itens.add(mapearItem(rsItens))
                    }
                }

                return venda.copy(itens = itens)
            }
        }
    }

    // Estorna uma venda: remove os itens, remove a venda e devolve o estoque.
    fun estornar(id: Int): Boolean {
        val sqlBuscaItens = "SELECT caixa_da_agua_id, quantidade FROM VENDA_ITEM WHERE venda_id = ?"
        val sqlDeletaItens = "DELETE FROM VENDA_ITEM WHERE venda_id = ?"
        val sqlDeletaVenda = "DELETE FROM VENDA WHERE id = ?"
        val sqlDevolveEstoque = "UPDATE CAIXA_DA_AGUA SET quantidade = quantidade + ? WHERE id = ?"

        DbConnection.conectar().use { conn ->
            conn.autoCommit = false

            try {
                // Descobre quais produtos precisam voltar ao estoque.
                val produtos = mutableListOf<Pair<Int, Int>>()
                conn.prepareStatement(sqlBuscaItens).use { stmt ->
                    stmt.setInt(1, id)
                    val rs = stmt.executeQuery()
                    while (rs.next()) {
                        val caixaId = rs.getObject("caixa_da_agua_id") as? Int
                        val qtd = rs.getInt("quantidade")
                        if (caixaId != null) {
                            produtos.add(caixaId to qtd)
                        }
                    }
                    rs.close()
                }

                // Devolve os produtos ao estoque.
                for ((caixaId, qtd) in produtos) {
                    conn.prepareStatement(sqlDevolveEstoque).use { stmt ->
                        stmt.setInt(1, qtd)
                        stmt.setInt(2, caixaId)
                        stmt.executeUpdate()
                    }
                }

                // Deleta itens e venda.
                conn.prepareStatement(sqlDeletaItens).use { stmt ->
                    stmt.setInt(1, id)
                    stmt.executeUpdate()
                }

                val deletado =
                    conn.prepareStatement(sqlDeletaVenda).use { stmt ->
                        stmt.setInt(1, id)
                        stmt.executeUpdate() > 0
                    }

                conn.commit()
                return deletado
            } catch (e: Exception) {
                conn.rollback()
                throw e
            } finally {
                conn.autoCommit = true
            }
        }
    }
}
