package financeiro

import database.DbConnection
import model.MovimentacaoFinanceira
import model.TipoMovimentacao
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp

// Repository do modulo financeiro.
// Registra entradas e saidas de dinheiro no caixa, como vendas, compras e pagamento de salarios.
object FinanceiroRepository {

    private fun mapearMovimentacao(rs: ResultSet): MovimentacaoFinanceira {
        return MovimentacaoFinanceira(
            id = rs.getInt("id"),
            valor = rs.getDouble("valor"),
            pagador = rs.getString("pagador"),
            recebedor = rs.getString("recebedor"),
            dataHora = rs.getTimestamp("data_hora").toLocalDateTime(),
            motivo = rs.getString("motivo"),
            responsavel = rs.getString("responsavel"),
            tipo = TipoMovimentacao.valueOf(rs.getString("tipo"))
        )
    }

    // Registra uma nova movimentacao financeira e retorna o ID gerado.
    fun registrar(mov: MovimentacaoFinanceira): Int {
        val sql = """
            INSERT INTO MOVIMENTACAO_FINANCEIRA (
                valor, pagador, recebedor, data_hora, motivo, responsavel, tipo
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.setDouble(1, mov.valor)
                stmt.setString(2, mov.pagador)
                stmt.setString(3, mov.recebedor)
                stmt.setTimestamp(4, Timestamp.valueOf(mov.dataHora))
                stmt.setString(5, mov.motivo)
                stmt.setString(6, mov.responsavel)
                stmt.setString(7, mov.tipo.name)
                stmt.executeUpdate()

                val rs = stmt.generatedKeys
                rs.next()
                return rs.getInt(1)
            }
        }
    }

    // Lista todas as movimentacoes financeiras, da mais nova para a mais antiga.
    fun listar(): List<MovimentacaoFinanceira> {
        val sql = "SELECT * FROM MOVIMENTACAO_FINANCEIRA ORDER BY data_hora DESC"
        val movimentacoes = mutableListOf<MovimentacaoFinanceira>()

        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    movimentacoes.add(mapearMovimentacao(rs))
                }
            }
        }

        return movimentacoes
    }

    // Calcula o saldo do caixa somando entradas e subtraindo saidas.
    fun calcularSaldo(): Double {
        val sql = """
            SELECT
                COALESCE(SUM(CASE WHEN tipo = 'ENTRADA' THEN valor ELSE 0 END), 0) as entradas,
                COALESCE(SUM(CASE WHEN tipo = 'SAIDA' THEN valor ELSE 0 END), 0) as saidas
            FROM MOVIMENTACAO_FINANCEIRA
        """.trimIndent()

        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                val rs = stmt.executeQuery()
                rs.next()
                return rs.getDouble("entradas") - rs.getDouble("saidas")
            }
        }
    }
}
