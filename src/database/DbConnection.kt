package database

import java.sql.Connection
import java.sql.DriverManager

object DbConnection {
    private val url: String =
        "jdbc:postgresql://dpg-da26n9fqj5pc73dmoi1g-a.oregon-postgres.render.com/caixa_da_agua?sslmode=require"
    private val user: String = "gerente"
    private val senha: String = "jhv0YfevlPgTUocEOX2udiXoBpIrim06"

    // Retorna a conexão ao invés de guardar numa variável global mutável
    fun conectar(): Connection {
        return DriverManager.getConnection(url, user, senha).also {
            println("Conexão Estabelecida")
        }
    }
}