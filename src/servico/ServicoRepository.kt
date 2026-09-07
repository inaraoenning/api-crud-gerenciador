package servico

import database.DbConnection
import model.Servico
import java.sql.PreparedStatement
import java.sql.ResultSet

// Repository para a tabela SERVICO.
// Faz o CRUD basico de servicos oferecidos.
object ServicoRepository {

    private fun mapearServico(rs: ResultSet): Servico {
        return Servico(
            id = rs.getInt("id"),
            nome = rs.getString("nome"),
            descricao = rs.getString("descricao"),
            preco = rs.getDouble("preco")
        )
    }

    // Cadastra um novo servico e retorna o ID gerado.
    fun inserir(servico: Servico): Int {
        val sql = "INSERT INTO SERVICO (nome, descricao, preco) VALUES (?, ?, ?)"

        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.setString(1, servico.nome)
                stmt.setString(2, servico.descricao)
                stmt.setDouble(3, servico.preco)
                stmt.executeUpdate()

                val rs = stmt.generatedKeys
                rs.next()
                return rs.getInt(1)
            }
        }
    }

    // Lista todos os servicos cadastrados.
    fun listar(): List<Servico> {
        val sql = "SELECT * FROM SERVICO"
        val servicos = mutableListOf<Servico>()

        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    servicos.add(mapearServico(rs))
                }
            }
        }

        return servicos
    }

    // Busca um servico pelo ID. Retorna null se nao existir.
    fun buscarPorId(id: Int): Servico? {
        val sql = "SELECT * FROM SERVICO WHERE id = ?"

        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                val rs = stmt.executeQuery()
                return if (rs.next()) mapearServico(rs) else null
            }
        }
    }

    // Atualiza os dados de um servico existente.
    fun atualizar(servico: Servico): Boolean {
        val sql = "UPDATE SERVICO SET nome = ?, descricao = ?, preco = ? WHERE id = ?"

        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, servico.nome)
                stmt.setString(2, servico.descricao)
                stmt.setDouble(3, servico.preco)
                stmt.setInt(4, servico.id)
                return stmt.executeUpdate() > 0
            }
        }
    }

    // Remove um servico pelo ID.
    fun deletar(id: Int): Boolean {
        val sql = "DELETE FROM SERVICO WHERE id = ?"

        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                return stmt.executeUpdate() > 0
            }
        }
    }
}
