package caixadaagua

import database.DbConnection
import enums.CorCaixa
import enums.Formato
import enums.MarcaCaixa
import enums.Material
import java.sql.PreparedStatement
import java.sql.ResultSet
import model.CaixaDagua

// Classe que faz a ponte entre o programa e a tabela CAIXA_DA_AGUA do banco.
// Aqui ficam as operacoes de CRUD: inserir, listar, buscar, atualizar e deletar.
object CaixaDaAguaRepository {

    // Converte uma linha do banco em um objeto CaixaDagua.
    // Usamos LEFT JOIN com FORNECEDOR para trazer o nome do fornecedor junto.
    private fun mapearCaixa(rs: ResultSet): CaixaDagua {
        return CaixaDagua(
            id = rs.getInt("id"),
            marca = MarcaCaixa.valueOf(rs.getString("marca")),
            modelo = rs.getString("modelo"),
            capacidadeLitros = rs.getInt("capacidade_litros"),
            largura = rs.getDouble("largura"),
            altura = rs.getDouble("altura"),
            profundidade = rs.getDouble("profundidade"),
            cor = CorCaixa.valueOf(rs.getString("cor")),
            material = Material.valueOf(rs.getString("material")),
            formato = Formato.valueOf(rs.getString("formato")),
            preco = rs.getDouble("preco"),
            quantidade = rs.getInt("quantidade"),
            fornecedorId = rs.getInt("fornecedor_id"),
        )
    }

    // Preenche os valores comuns de uma caixa d'agua num PreparedStatement,
    // tanto para inserir quanto para atualizar.
    private fun preencherStatement(stmt: PreparedStatement, caixa: CaixaDagua, inicio: Int) {
        stmt.setString(inicio, caixa.marca.name)
        stmt.setString(inicio + 1, caixa.modelo)
        stmt.setInt(inicio + 2, caixa.capacidadeLitros)
        stmt.setDouble(inicio + 3, caixa.largura)
        stmt.setDouble(inicio + 4, caixa.altura)
        stmt.setDouble(inicio + 5, caixa.profundidade)
        stmt.setString(inicio + 6, caixa.cor.name)
        stmt.setString(inicio + 7, caixa.material.name)
        stmt.setString(inicio + 8, caixa.formato.name)
        stmt.setDouble(inicio + 9, caixa.preco)
        stmt.setInt(inicio + 10, caixa.quantidade)
        stmt.setInt(inicio + 11, caixa.fornecedorId)
    }

    // Cadastra uma nova caixa d'agua no banco e devolve o ID gerado.
    fun inserir(caixa: CaixaDagua): Int {
        val sql =
            """
            INSERT INTO CAIXA_DA_AGUA (
                marca, modelo, capacidade_litros, largura, altura,
                profundidade, cor, material, formato, preco, quantidade, fornecedor_id
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """
                .trimIndent()

        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS).use { stmt ->
                preencherStatement(stmt, caixa, 1)
                stmt.executeUpdate()

                val rs = stmt.generatedKeys
                rs.next()
                return rs.getInt(1)
            }
        }
    }

    // Lista todas as caixas d'agua cadastradas.
    fun listar(): List<CaixaDagua> {
        val sql =
            """
            SELECT c.*
            FROM CAIXA_DA_AGUA c
            LEFT JOIN FORNECEDOR fr ON fr.pessoa_id = c.fornecedor_id

            """
                .trimIndent()

        val caixas = mutableListOf<CaixaDagua>()
        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    caixas.add(mapearCaixa(rs))
                }
            }
        }
        return caixas
    }

    // Busca uma caixa d'agua pelo ID. Retorna null se nao encontrar.
    fun buscarPorId(id: Int): CaixaDagua? {
        val sql =
            """
            SELECT c.*
            FROM CAIXA_DA_AGUA c
            LEFT JOIN FORNECEDOR fr ON fr.pessoa_id = c.fornecedor_id
            WHERE c.id = ?
            """
                .trimIndent()

        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                val rs = stmt.executeQuery()
                return if (rs.next()) mapearCaixa(rs) else null
            }
        }
    }

    // Atualiza os dados de uma caixa d'agua existente.
    fun atualizar(caixa: CaixaDagua): Boolean {
        val sql =
            """
            UPDATE CAIXA_DA_AGUA SET
                marca = ?, modelo = ?, capacidade_litros = ?, largura = ?,
                altura = ?, profundidade = ?, cor = ?, material = ?, formato = ?,
                preco = ?, quantidade = ?, fornecedor_id = ?
            WHERE id = ?
            """
                .trimIndent()

        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                preencherStatement(stmt, caixa, 1)
                stmt.setInt(13, caixa.id)
                return stmt.executeUpdate() > 0
            }
        }
    }

    // Remove uma caixa d'agua do banco pelo ID.
    fun deletar(id: Int): Boolean {
        val sql = "DELETE FROM CAIXA_DA_AGUA WHERE id = ?"
        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                return stmt.executeUpdate() > 0
            }
        }
    }
}
