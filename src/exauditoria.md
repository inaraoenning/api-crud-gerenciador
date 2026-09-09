Exemplo de função de auditoria
Uma função útil seria relatorioDeVendasPorFuncionario, que mostra quanto cada funcionário vendeu e ajuda a auditar.

VendaRepository:

fun relatorioDeVendasPorFuncionario(): Map<Int, Double> {
val sql = "SELECT funcionario_id, SUM(valor_total) as total FROM VENDA GROUP BY funcionario_id"
val relatorio = mutableMapOf<Int, Double>()
DbConnection.conectar().use { conn ->
conn.prepareStatement(sql).use { stmt ->
val rs = stmt.executeQuery()
while (rs.next()) {
relatorio[rs.getInt("funcionario_id")] = rs.getDouble("total")
}
}
}
return relatorio
}

FinanceiroController

val relatorio = VendaRepository.relatorioDeVendasPorFuncionario()
relatorio.forEach { (id, total) -> println("Funcionario $id vendeu R$ $total") }
