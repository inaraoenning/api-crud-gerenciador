package database

import java.sql.Connection
import java.sql.DriverManager

object DbConnection {
    private val url: String =
        ""
    private val user: String = ""
    private val senha: String = ""

    // Retorna a conexão ao invés de guardar numa variável global mutável
    fun conectar(): Connection {
        return DriverManager.getConnection(url, user, senha).also {
            println("Conexão Estabelecida")
        }
    }
}
