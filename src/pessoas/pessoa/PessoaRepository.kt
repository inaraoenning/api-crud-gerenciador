package pessoas.pessoa

import database.DbConnection
import enums.Setor
import java.math.BigDecimal
import java.sql.PreparedStatement
import java.sql.ResultSet
import model.Cliente
import model.Fornecedor
import model.Funcionario
import model.Pessoa
import model.TipoPessoa

class PessoaRepository private constructor(val dbConnection: DbConnection) {

    companion object {
        @Volatile private var instancia: PessoaRepository? = null

        fun getInstancia(): PessoaRepository {
            return instancia
                ?: synchronized(this) {
                    instancia ?: PessoaRepository(DbConnection).also { instancia = it }
                }
        }
    }

    private fun mapearPessoa(rs: ResultSet): Pessoa {
        val id = rs.getInt("id")
        val nome = rs.getString("nome")
        val documento = rs.getString("cpf_cnpj")
        val telefone = rs.getString("telefone")
        val tipo = rs.getString("tipo")
        val ativo = rs.getBoolean("ativo")

        // Cria o objeto especifico de cada tipo, passando o ativo corretamente.
        return when (TipoPessoa.valueOf(tipo)) {
            TipoPessoa.FUNCIONARIO -> {
                val setor = rs.getString("setor") ?: Setor.FINANCEIRO.name
                val salario = rs.getBigDecimal("salario") ?: BigDecimal.ZERO
                Funcionario(
                    idFuncionario = id,
                    nomeFuncionario = nome,
                    documentoFuncionario = documento,
                    telefoneFuncionario = telefone,
                    setor = Setor.valueOf(setor),
                    salario = salario,
                ).apply { this.ativo = ativo }
            }
            TipoPessoa.FORNECEDOR ->
                Fornecedor(
                    idFornecedor = id,
                    nomeFornecedor = nome,
                    documentoFornecedor = documento,
                    telefoneFornecedor = telefone,
                ).apply { this.ativo = ativo }
            TipoPessoa.CLIENTE ->
                Cliente(
                    idCliente = id,
                    nomeCliente = nome,
                    documentoCliente = documento,
                    telefoneCliente = telefone,
                ).apply { this.ativo = ativo }
        }
    }

    // Verifica se ja existe uma pessoa com o mesmo CPF/CNPJ no banco.
    // Usada para evitar documentos duplicados no cadastro.
    private fun documentoJaExiste(documento: String): Boolean {
        val sql = "SELECT 1 FROM PESSOA WHERE cpf_cnpj = ?"
        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, documento)
                val rs = stmt.executeQuery()
                return rs.next()
            }
        }
    }

    fun inserir(
        pessoa: Pessoa,
        salario: Double = 0.0,
        setor: String? = null,
        limiteCredito: Double = 0.0,
        razaoSocial: String? = null,
    ): Boolean {
        // Verifica se o CPF/CNPJ ja existe no banco antes de cadastrar.
        if (documentoJaExiste(pessoa.documento)) {
            println("Erro: ja existe uma pessoa cadastrada com o documento ${pessoa.documento}.")
            return false
        }

        DbConnection.conectar().use { conn ->
            conn.autoCommit = false

            val sqlPessoa =
                """
                INSERT INTO PESSOA (nome, cpf_cnpj, telefone, tipo, ativo)
                VALUES (?, ?, ?, ?, ?)
                """
                    .trimIndent()

            conn.prepareStatement(sqlPessoa, PreparedStatement.RETURN_GENERATED_KEYS).use { stmt ->
                stmt.setString(1, pessoa.nome)
                stmt.setString(2, pessoa.documento)
                stmt.setString(3, pessoa.telefone)
                stmt.setString(4, pessoa.tipo.name)
                stmt.setBoolean(5, pessoa.ativo)
                stmt.executeUpdate()

                val rs = stmt.generatedKeys
                rs.next()
                val pessoaId = rs.getInt(1)

                when (pessoa.tipo) {
                    TipoPessoa.FUNCIONARIO -> {
                        val sql =
                            "INSERT INTO FUNCIONARIO (pessoa_id, salario, setor) VALUES (?, ?, ?)"
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
        val sql =
            """
            SELECT p.*, f.setor, f.salario, c.limite_credito, fn.razao_social
            FROM PESSOA p
            LEFT JOIN FUNCIONARIO f ON f.pessoa_id = p.id
            LEFT JOIN CLIENTE c ON c.pessoa_id = p.id
            LEFT JOIN FORNECEDOR fn ON fn.pessoa_id = p.id
            """
                .trimIndent()

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
        val sql =
            """
            SELECT p.*, f.setor, f.salario, c.limite_credito, fn.razao_social
            FROM PESSOA p
            LEFT JOIN FUNCIONARIO f ON f.pessoa_id = p.id
            LEFT JOIN CLIENTE c ON c.pessoa_id = p.id
            LEFT JOIN FORNECEDOR fn ON fn.pessoa_id = p.id
            WHERE p.id = ?
            """
                .trimIndent()

        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, id)
                val rs = stmt.executeQuery()
                return if (rs.next()) mapearPessoa(rs) else null
            }
        }
    }

    fun listarAtivos(): List<Pessoa> {
        val pessoasAtivas = mutableListOf<Pessoa>()
        val sql =
            """
            SELECT p.*, f.setor, f.salario, c.limite_credito, fn.razao_social
            FROM PESSOA p
            LEFT JOIN FUNCIONARIO f ON f.pessoa_id = p.id
            LEFT JOIN CLIENTE c ON c.pessoa_id = p.id
            LEFT JOIN FORNECEDOR fn ON fn.pessoa_id = p.id
            WHERE p.ativo = TRUE
            """
                .trimIndent()

        DbConnection.conectar().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    pessoasAtivas.add(mapearPessoa(rs))
                }
            }
        }

        return pessoasAtivas
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
