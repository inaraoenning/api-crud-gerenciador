package pessoa

import config.DbConnection
import model.Cliente
import model.Fornecedor
import model.Funcionario
import model.Pessoa
import model.Setor
import model.TipoPessoa
import java.sql.PreparedStatement
import java.sql.ResultSet

class PessoaRepository {

    private fun mapearPessoa(rs: ResultSet): Pessoa {
        val id = rs.getInt("id")
        val nome = rs.getString("nome")
        val documento = rs.getString("cpf_cnpj")
        val telefone = rs.getString("telefone")
        val email = rs.getString("email")
        val tipo = rs.getString("tipo")
        val ativo = rs.getBoolean("ativo")

        return when (TipoPessoa.valueOf(tipo)) {
            TipoPessoa.FUNCIONARIO -> {
                val setor = rs.getString("setor") ?: Setor.FINANCEIRO.name
                Funcionario(
                    idFuncionario = id,
                    nomeFuncionario = nome,
                    documentoFuncionario = documento,
                    telefoneFuncionario = telefone,
                    emailFuncionario = email,
                    setor = Setor.valueOf(setor)
                )
            }
            TipoPessoa.FORNECEDOR -> Fornecedor(
                idFornecedor = id,
                nomeFornecedor = nome,
                documentoFornecedor = documento,
                telefoneFornecedor = telefone,
                emailFornecedor = email
            )
            TipoPessoa.CLIENTE -> Cliente(
                idCliente = id,
                nomeCliente = nome,
                documentoCliente = documento,
                telefoneCliente = telefone,
                emailCliente = email
            )
        }
    }

    fun inserir(pessoa: Pessoa, salario: Double = 0.0, setor: String? = null, limiteCredito: Double = 0.0, razaoSocial: String? = null): Boolean {
        DbConnection.conectar().use { conn ->
            conn.autoCommit = false

            val sqlPessoa = """
                INSERT INTO PESSOA (nome, cpf_cnpj, telefone, email, tipo, ativo)
                VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent()

            conn.prepareStatement(sqlPessoa, PreparedStatement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.setString(1, pessoa.nome)
                stmt.setString(2, pessoa.documento)
                stmt.setString(3, pessoa.telefone)
                stmt.setString(4, pessoa.email)
                stmt.setString(5, pessoa.tipo.name)
                stmt.setBoolean(6, pessoa.ativo)
                stmt.executeUpdate()

                val rs = stmt.generatedKeys
                rs.next()
                val pessoaId = rs.getInt(1)

                when (pessoa.tipo) {
                    TipoPessoa.FUNCIONARIO -> {
                        val sql = "INSERT INTO FUNCIONARIO (pessoa_id, salario, setor) VALUES (?, ?, ?)"
                        conn.prepareStatement(sql).use {
                            it.setInt(1, pessoaId)
                            it.setDouble(2, salario)
                            it.setString(3, setor ?: Setor.FINANCEIRO.name)
                            it.executeUpdate()
                        }
                    }
                    TipoPessoa.CLIENTE -> {
                        val sql = "INSERT INTO CLIENTE (pessoa_id, limite_credito) VALUES (?, ?)"
                        conn.prepareStatement(sql).use {
                            it.setInt(1, pessoaId)
                            it.setDouble(2, limiteCredito)
                            it.executeUpdate()
                        }
                    }
                    TipoPessoa.FORNECEDOR -> {
                        val sql = "INSERT INTO FORNECEDOR (pessoa_id, razao_social) VALUES (?, ?)"
                        conn.prepareStatement(sql).use {
                            it.setInt(1, pessoaId)
                            it.setString(2, razaoSocial ?: pessoa.nome)
                            it.executeUpdate()
                        }
                    }
                }

                conn.commit()
                return true
            }
        }
    }

    fun listar(): List<Pessoa> {
        val pessoas = mutableListOf<Pessoa>()
        val sql = """
            SELECT p.*, f.setor, c.limite_credito, fn.razao_social
            FROM PESSOA p
            LEFT JOIN FUNCIONARIO f ON f.pessoa_id = p.id
            LEFT JOIN CLIENTE c ON c.pessoa_id = p.id
            LEFT JOIN FORNECEDOR fn ON fn.pessoa_id = p.id
            WHERE p.ativo = TRUE
        """.trimIndent()

        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    pessoas.add(mapearPessoa(rs))
                }
            }
        }

        return pessoas
    }

    fun buscarPorId(id: Int): Pessoa? {
        val sql = """
            SELECT p.*, f.setor, c.limite_credito, fn.razao_social
            FROM PESSOA p
            LEFT JOIN FUNCIONARIO f ON f.pessoa_id = p.id
            LEFT JOIN CLIENTE c ON c.pessoa_id = p.id
            LEFT JOIN FORNECEDOR fn ON fn.pessoa_id = p.id
            WHERE p.id = ?
        """.trimIndent()

        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                val rs = stmt.executeQuery()
                return if (rs.next()) mapearPessoa(rs) else null
            }
        }
    }

    fun inativar(id: Int): Boolean {
        val sql = "UPDATE PESSOA SET ativo = FALSE WHERE id = ?"
        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                return stmt.executeUpdate() > 0
            }
        }
    }
}