package database

import java.sql.Connection
import java.sql.DriverManager

object DbConnection {
    private val url: String =
        "jdbc:postgresql://dpg-dafuit7qj5pc738e0kbg-a.oregon-postgres.render.com/dbgerenciador?sslmode=require"
    private val user: String = "admin"
    private val senha: String = "RgI9R6NyTQJQmEyGOkquMnWMeS7eN4u1"

    // Retorna a conexão ao invés de guardar numa variável global mutável
    fun conectar(): Connection {
        return DriverManager.getConnection(url, user, senha).also {
            println("Conexão Estabelecida")
        }
    }
}