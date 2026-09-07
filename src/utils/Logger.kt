package utils

import database.DbConnection

// Logger simples do sistema.
// Grava mensagens na tabela LOG_SISTEMA do banco para auditoria.
// Cada log tem um nivel (INFO, ERRO, WARN) e a mensagem.
object Logger {

    // Niveis de log disponiveis.
    enum class Nivel {
        INFO, ERRO, WARN
    }

    // Registra uma mensagem no banco.
    fun log(nivel: Nivel, mensagem: String) {
        val sql = "INSERT INTO LOG_SISTEMA (nivel, mensagem) VALUES (?, ?)"
        try {
            DbConnection.conectar().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, nivel.name)
                    stmt.setString(2, mensagem)
                    stmt.executeUpdate()
                }
            }
        } catch (e: Exception) {
            // Se der erro ao gravar no banco, pelo menos mostra no console.
            println("[FALHA AO GRAVAR LOG] $nivel: $mensagem - ${e.message}")
        }
    }

    // Metodos de conveniencia.
    fun info(mensagem: String) = log(Nivel.INFO, mensagem)
    fun erro(mensagem: String) = log(Nivel.ERRO, mensagem)
    fun warn(mensagem: String) = log(Nivel.WARN, mensagem)
}
