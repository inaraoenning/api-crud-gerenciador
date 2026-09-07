package pessoas.funcionario

import database.DbConnection
import enums.Setor
import model.Funcionario
import java.math.BigDecimal
import java.sql.ResultSet

// Repository especifico para funcionarios.
// Apesar de o cadastro basico ficar no PessoaRepository, aqui ficam consultas
// uteis para buscar, listar e filtrar funcionarios por setor.
object FuncionarioRepository {

    private fun mapearFuncionario(rs: ResultSet): Funcionario {
        return Funcionario(
            idFuncionario = rs.getInt("pessoa_id"),
            nomeFuncionario = rs.getString("nome"),
            documentoFuncionario = rs.getString("cpf_cnpj"),
            telefoneFuncionario = rs.getString("telefone"),
            salario = rs.getBigDecimal("salario") ?: BigDecimal.ZERO,
            setor = Setor.valueOf(rs.getString("setor") ?: "FINANCEIRO")
        ).apply {
            ativo = rs.getBoolean("ativo")
        }
    }

    // Lista todos os funcionarios ativos do banco.
    fun listarAtivos(): List<Funcionario> {
        val sql = """
            SELECT p.id as pessoa_id, p.nome, p.cpf_cnpj, p.telefone, p.ativo, f.salario, f.setor
            FROM PESSOA p
            JOIN FUNCIONARIO f ON f.pessoa_id = p.id
            WHERE p.ativo = TRUE
        """.trimIndent()

        val funcionarios = mutableListOf<Funcionario>()
        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    funcionarios.add(mapearFuncionario(rs))
                }
            }
        }
        return funcionarios
    }

    // Lista funcionarios filtrados por setor.
    fun listarPorSetor(setor: Setor): List<Funcionario> {
        val sql = """
            SELECT p.id as pessoa_id, p.nome, p.cpf_cnpj, p.telefone, p.ativo, f.salario, f.setor
            FROM PESSOA p
            JOIN FUNCIONARIO f ON f.pessoa_id = p.id
            WHERE p.ativo = TRUE AND f.setor = ?
        """.trimIndent()

        val funcionarios = mutableListOf<Funcionario>()
        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, setor.name)
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    funcionarios.add(mapearFuncionario(rs))
                }
            }
        }
        return funcionarios
    }

    // Busca um funcionario ativo pelo ID da pessoa.
    fun buscarPorId(id: Int): Funcionario? {
        val sql = """
            SELECT p.id as pessoa_id, p.nome, p.cpf_cnpj, p.telefone, p.ativo, f.salario, f.setor
            FROM PESSOA p
            JOIN FUNCIONARIO f ON f.pessoa_id = p.id
            WHERE p.id = ? AND p.ativo = TRUE
        """.trimIndent()

        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                val rs = stmt.executeQuery()
                return if (rs.next()) mapearFuncionario(rs) else null
            }
        }
    }
}
