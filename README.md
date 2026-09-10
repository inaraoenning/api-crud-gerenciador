# Sistema de Gerenciamento de Caixas d'Agua

Aplicacao simples em Kotlin para gerenciar uma loja de caixas d'agua. Feita no terminal, usando JDBC puro com banco PostgreSQL.

## Tecnologias

- Kotlin
- JDBC
- PostgreSQL

## O que o sistema faz

- Cadastra e gerencia pessoas (funcionarios, clientes e fornecedores)
- Controla o estoque de caixas d'agua
- Cadastra servicos oferecidos
- Realiza vendas na frente de caixa
- Controla movimentacoes financeiras
- Registra logs de operacoes no banco

## Como rodar

1. Certifique-se de ter o PostgreSQL instalado e rodando.
2. Crie o banco de dados e as tabelas usando o script SQL do trabalho.
3. Configure a conexao no arquivo `src/database/DbConnection.kt`.
4. Adicione o driver JDBC do PostgreSQL no classpath do projeto.
5. Execute a classe `Main.kt` no IntelliJ ou via linha de comando.

## Menu principal

```
1 - Gerenciar Pessoas
2 - Gerenciar Estoque
3 - Gerenciar Servicos
4 - Frente de Caixa
5 - Financeiro
0 - Sair
```

### Gerenciar Pessoas
- Funcionarios
- Fornecedores
- Clientes
- Listar todas as pessoas
- Inativar pessoa

### Gerenciar Estoque
- Visualizar estoque
- Adicionar caixa d'agua
- Editar caixa d'agua
- Deletar caixa d'agua

### Gerenciar Servicos
- Cadastrar servico
- Listar servicos
- Editar servico
- Deletar servico

### Frente de Caixa
- Realizar venda
- Estornar venda
- Historico de vendas

### Financeiro
- Pagar salario
- Registrar compra/despesa
- Registrar entrada manual
- Historico de movimentacoes
- Saldo do caixa

## Estrutura de pastas

```
src/
├── Main.kt
├── database/
│   └── DbConnection.kt
├── model/
│   ├── Pessoa.kt
│   ├── Funcionario.kt
│   ├── Cliente.kt
│   ├── Fornecedor.kt
│   ├── CaixaDagua.kt
│   ├── Servico.kt
│   ├── Venda.kt
│   ├── ItemVenda.kt
│   └── MovimentacaoFinanceira.kt
├── enums/
│   ├── Setor.kt
│   ├── TipoPessoa.kt
│   ├── Material.kt
│   └── Formato.kt
├── pessoas/
│   ├── pessoa/
│   ├── funcionario/
│   ├── cliente/
│   └── fornecedor/
├── caixadaagua/
├── servico/
├── venda/
├── financeiro/
└── utils/
    ├── validator.kt
    └── Logger.kt
```

## Observacoes

- O projeto nao usa frameworks web, e uma aplicacao console.
- A conexao com o banco esta centralizada em `DbConnection.kt`.
- Cada modulo tem seu proprio repository e controller, separando responsabilidades.
