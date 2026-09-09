# Roadmap de Testes - Sistema de Gerenciamento de Caixas d'Agua

Esse documento serve para voce testar o sistema e se preparar para as perguntas do professor.

---

## 1. Classe e atributos
**Arquivo:** `src/model/CaixaDagua.kt`

Classe escolhida: `CaixaDagua`
- Atributos: `id`, `marca`, `modelo`, `capacidadeLitros`, `largura`, `altura`, `profundidade`, `cor`, `material`, `formato`, `preco`, `quantidade`, `fornecedorId`
- Escolhi esses atributos porque sao as colunas do banco e definem um produto de estoque.
- Se tirar `fornecedorId`, nao sabemos de quem foi comprada a caixa. Se tirar `quantidade`, nao sabemos o estoque disponivel.

**Teste:** cadastre uma caixa e veja se ela aparece no estoque com todos os dados.

---

## 2. Heranca ou interfaces
**Arquivos:** `src/model/Pessoa.kt`, `src/model/Funcionario.kt`, `src/model/Cliente.kt`, `src/model/Fornecedor.kt`

- `Funcionario`, `Cliente` e `Fornecedor` herdam de `Pessoa`.
- Foi a melhor solucao porque evita repetir `id`, `nome`, `documento`, `telefone` e `ativo` em cada classe.

**Teste:** cadastre um funcionario, cliente e fornecedor. Veja que todos ficam na tabela `PESSOA` e tem uma tabela especifica com dados a mais.

---

## 3. Polimorfismo
**Arquivo:** `src/pessoas/PessoaRepository.kt` (metodo `mapearPessoa`)

- O metodo retorna `Pessoa`, mas cria `Funcionario`, `Cliente` ou `Fornecedor` conforme o campo `tipo` do banco.
- O mesmo metodo se comporta diferente dependendo do dado lido.

**Teste:** liste fornecedores e funcionarios. Veja que o sistema mostra informacoes diferentes para cada um (ex: salario para funcionario, marca para fornecedor).

---

## 4. Encapsulamento com dinheiro/estoque
**Arquivo:** `src/financeiro/FinanceiroRepository.kt` (metodo `temSaldo`)

- A regra "nao deixa o saldo ficar negativo" fica dentro do repository.
- Antes de pagar salario ou comprar estoque, o sistema pergunta se tem saldo.

**Teste:** nao cadastre nenhuma entrada financeira e tente pagar um salario. O sistema deve recusar.

---

## 5. Novo tipo de funcionario (Estagiario)
Para criar `Estagiario`, precisaria alterar:
1. `src/model/TipoPessoa.kt` - adicionar `ESTAGIARIO`.
2. `src/model/Estagiario.kt` - nova classe herdando de `Pessoa`.
3. `src/pessoas/PessoaRepository.kt` - mapear o novo tipo e inserir na tabela correta.
4. `src/pessoas/funcionario/FuncionarioController.kt` ou novo controller.
5. `src/main/resources/schema.sql` - criar tabela `ESTAGIARIO`.

---

## 6. Enum class
**Arquivos:** `src/enums/Setor.kt`, `src/enums/Material.kt`, `src/enums/Formato.kt`, `src/enums/CorCaixa.kt`, `src/enums/MarcaCaixa.kt`, `src/model/TipoPessoa.kt`, `src/model/TipoMovimentacao.kt`

- Usamos enum para limitar opcoes (ex: cor so pode ser Azul, Bege ou Preta).
- Evita erros de digitacao e deixa o menu com numeros faceis.

**Teste:** tente digitar uma opcao invalida no menu de cor. O sistema recusa.

---

## 7. Arquitetura em camadas
Packages:
- `model/` - classes que representam os dados (ex: `CaixaDagua`, `Pessoa`).
- `database/` - conexao com o PostgreSQL.
- `pessoas/`, `caixadaagua/`, `venda/`, `financeiro/`, `servico/` - controllers e repositories por modulo.
- `utils/` - funcoes auxiliares como `Validador` e `Logger`.
- `enums/` - enumeradores.

Separei assim para cada parte do sistema ficar isolada e facil de achar.

---

## 8. Conexao com PostgreSQL
**Arquivo:** `src/database/DbConnection.kt`

- A conexao fica centralizada ali.
- Usa `DriverManager.getConnection(url, user, password)` e devolve uma `Connection`.
- Cada repository chama `DbConnection.conectar()`, executa e fecha com `.use`.

**Teste:** rode o programa. Se der erro de conexao, a mensagem aparece no console.

---

## 9. Trocar para MySQL
- Teoricamente so precisaria mudar `src/database/DbConnection.kt` (URL, driver, usuario, senha).
- Na pratica, tambem teria que revisar SQLs que usam sintaxe especifica do PostgreSQL (como `SERIAL`, `RETURNING`, `boolean`).

**Teste:** trocar a string de conexao e o JAR do driver.

---

## 10. Dependencia circular
- Nao existe dependencia circular.
- Evitei fazendo `PessoaRepository` usar `model`, e `controllers` usarem `PessoaRepository`.
- Nenhuma classe do `model` conhece repository.

---

## 11. Momento da movimentacao financeira
**Arquivos:** `src/financeiro/FinanceiroController.kt` e `src/venda/VendaController.kt`

- Venda: `realizarVenda()` calcula o total e chama `FinanceiroRepository.registrar()` com tipo `ENTRADA`.
- Salario: `pagarSalario()` cria movimentacao tipo `SAIDA`.
- Compra: `adicionarCaixa()` cria movimentacao tipo `SAIDA`.

Campos preenchidos:
- `valor`: valor da operacao
- `pagador`: quem pagou (ex: cliente, empresa)
- `recebedor`: quem recebeu (ex: empresa, funcionario)
- `motivo`: descricao da operacao
- `responsavel`: nome do funcionario ou "Setor Financeiro"
- `tipo`: `ENTRADA` ou `SAIDA`
- `dataHora`: gerado automaticamente

**Teste:** faca uma venda e depois consulte o historico financeiro.

---

## 12. Garantir que compra/venda gera fluxo de caixa
**Arquivos:** `src/venda/VendaController.kt`, `src/caixadaagua/CaixaDaAguaController.kt`

- Venda: sempre chama `FinanceiroRepository.registrar` ao final.
- Compra: pergunta valor e chama `FinanceiroRepository.registrar` apos inserir no estoque.
- Pagamento nao "some" porque e a unica maneira de atualizar o saldo.

**Teste:** faca uma venda, depois veja o saldo. Deve ter aumentado.

---

## 13. Concorrencia
- Sem lock, dois funcionarios podem vender a ultima caixa ao mesmo tempo.
- Na pratica, `VendaRepository` usa `SELECT ... FOR UPDATE`, que trava a linha do estoque durante a venda.
- Entretanto, isso so funciona se a transacao estiver ativa. Hoje a transacao e feita dentro do repository, entao e relativamente seguro.

**Teste:** abrir duas instancias do programa e tentar vender a mesma caixa ao mesmo tempo.

---

## 14. Relacionamento N para N
**Arquivos:** `src/model/Venda.kt`, `src/model/ItemVenda.kt`, `src/main/resources/schema.sql`

- Uma venda pode ter varios produtos e servicos; um produto/servico pode estar em varias vendas.
- No banco, a tabela `VENDA_ITEM` faz o meio do caminho, com `venda_id`, `caixa_da_agua_id` e `servico_id`.

**Teste:** crie uma venda com mais de um produto/servico.

---

## 15. Responsavel pela transacao
**Arquivo:** `src/financeiro/FinanceiroController.kt`

- O responsavel e salvo no campo `responsavel` da `MOVIMENTACAO_FINANCEIRA`.
- Venda: e o nome do funcionario que fez a venda.
- Salario: "Setor Financeiro".
- Compra: "Setor Financeiro".

**Teste:** consulte o historico de movimentacoes e veja a coluna responsavel.

---

## 16. Script de criacao das tabelas
**Arquivo:** `src/main/resources/schema.sql`

- Contem `CREATE TABLE` para `PESSOA`, `FUNCIONARIO`, `CLIENTE`, `FORNECEDOR`, `CAIXA_DA_AGUA`, `SERVICO`, `VENDA`, `VENDA_ITEM`, `MOVIMENTACAO_FINANCEIRA` e `LOG_SISTEMA`.
- Chaves estrangeiras ligam as tabelas filhas a `PESSOA` e `VENDA_ITEM` as tabelas de produto/servico.
- Escolhi `ON DELETE CASCADE` para remover dados filhos quando a pessoa for apagada.

**Teste:** rode o script no PostgreSQL e depois execute o programa.

---

## 17. Conexao do codigo com o banco
- `DbConnection.conectar()` abre a conexao.
- `use` fecha automaticamente.
- `prepareStatement` monta a SQL com `?`.
- `setString`, `setInt`, `setDouble` preenchem os parametros.
- `executeQuery` le, `executeUpdate` insere/atualiza/deleta.
- `ResultSet` e mapeado para objeto Kotlin.

**Teste:** cadastre um cliente e liste. Veja que o dado aparece no banco.

---

## 18. Conexao cair no meio da venda
**Arquivo:** `src/venda/VendaRepository.kt`

- Como a operacao usa `conn.autoCommit = false`, se der erro em qualquer ponto o `rollback()` e chamado.
- Entao ou a venda inteira e gravada, ou nada e.
- Dados nao ficam inconsistentes (venda sem itens ou sem baixa de estoque).

**Teste:** simular queda de conexao durante uma venda (difil, mas o codigo ja esta protegido).

---

## 19. Funcionario e setor
**Arquivo:** `src/main/resources/schema.sql` (tabela `FUNCIONARIO`)

```sql
CREATE TABLE FUNCIONARIO (
    pessoa_id INT PRIMARY KEY,
    salario DECIMAL(10, 2),
    setor VARCHAR(50)
);
```

- O setor e uma coluna na tabela `FUNCIONARIO`.
- Representado pelo enum `Setor` no Kotlin.

**Teste:** cadastre um funcionario e escolha o setor.

---

## 20. Funcionario mudar de setor
Precisa atualizar:
1. Coluna `setor` na tabela `FUNCIONARIO`.
2. Objeto `Funcionario` no Kotlin.
3. Criar uma opcao de editar funcionario no `FuncionarioController`.

Hoje nao tem tela de edicao.

---

## 21. Diferenciar Cliente de Fornecedor
**Arquivo:** `src/model/TipoPessoa.kt`

- Ambos herdam de `Pessoa`.
- O campo `tipo` define se e `CLIENTE` ou `FORNECEDOR`.
- `PessoaRepository` usa `TipoPessoa` para decidir qual objeto instanciar.

**Teste:** liste clientes e fornecedores. Veja que aparecem separados.

---

## 22. Regex usada
**Arquivo:** `src/utils/validator.kt`

Regex de CPF/CNPJ: `^\d{11}$|^\d{14}$`
- `^` = inicio da string
- `\d` = digito (0-9)
- `{11}` = exatamente 11 digitos
- `$` = fim da string
- `|` = ou
- `{14}` = exatamente 14 digitos

Regex de telefone: `^\(?\d{2}\)?\s?\d{4,5}-?\d{4}$`
- `\(?` = parentese de abertura opcional
- `\d{2}` = dois digitos (DDD)
- `\)?` = parentese de fechamento opcional
- `\s?` = espaco opcional
- `\d{4,5}` = quatro ou cinco digitos
- `-?` = hifen opcional
- `\d{4}` = quatro digitos finais

**Teste:** tente cadastrar documento `123` ou telefone `abc`. Deve recusar.

---

## 23. Entrada valida e invalida
- CPF valido: `12345678901` (11 numeros)
- CPF invalido: `123.456.789-01` (tem pontos e traco)
- Telefone valido: `(11) 98765-4321`
- Telefone invalido: `1198765` (faltam digitos)

---

## 24. Por que validar com regex
- Regex garante o formato exato.
- Verificacao so de tamanho nao impede letras ou simbolos errados.
- E mais seguro e previsivel.

---

## 25. Tipo nullable no modelo
**Arquivo:** `src/model/ItemVenda.kt`

```kotlin
val caixaDaguaId: Int?
val servicoId: Int?
```

- Sao nullable porque um item de venda pode ser um produto (caixa) OU um servico.
- O outro fica `null`.

**Teste:** crie uma venda com um produto e outra so com servico.

---

## 26. Not-null assertion `!!`
- Nao usamos `!!` no codigo.
- `!!` e arriscado porque lanca `NullPointerException` se a variavel for nula.
- Evitamos usando `?.` (safe call) e `?:` (elvis) para tratar nulidade com seguranca.

---

## 27. Diferenca entre `?:`, `?.` e `!!`
- `?.` (safe call): executa so se nao for nulo. Ex: `documento?.matches(regex)`.
- `?:` (elvis): retorna o valor da esquerda se nao for nulo, senao o da direita. Ex: `rs.getString("setor") ?: Setor.FINANCEIRO.name`.
- `!!` (not-null assertion): forca o acesso e pode dar erro se for nulo. **Nao usamos no projeto.**

**Arquivos:** `src/utils/validator.kt` e `src/pessoas/PessoaRepository.kt`.

---

## 28. Enter em campo obrigatorio
- Hoje, se apertar Enter no `nome`, ainda aceita string vazia.
- Nos campos de numero, `toIntOrNull` e `toDoubleOrNull` retornam `null` e a funcao `ler...Seguro` repete ou cancela.
- No preco e quantidade, o sistema exige valor > 0.

**Teste:** aperte Enter sem digitar no preco. O sistema repete a pergunta.

---

## 29. Pontos que ainda podem quebrar
- Nome em branco ainda e aceito em alguns cadastros.
- Quantidade de caixa pode ser digitada como 0 (depende do `lerInteiroSeguro`).
- Telefone regex aceita formatos variados, mas nao valida DDD real.

---

## 30. Simulacoes
**Cadastrar funcionario com salario negativo:**
- `lerDoubleSeguro` so aceita `> 0`, entao recusa.

**CPF errado:**
- `Validador.documentoValido` recusa se nao tiver 11 ou 14 digitos.

**Nao finalizar cadastro:**
- Se o usuario digitar dados invalidos, o `lerDadosComunsPessoa` fica em loop.
- Se o controller receber `null`, a operacao e cancelada (`return`).

---

## Checklist rapido para testar tudo

1. Rodar `src/main/resources/schema.sql` no PostgreSQL.
2. Compilar o projeto com o comando que voce ja usa.
3. Executar `Main.kt`.
4. Cadastrar um funcionario.
5. Cadastrar um cliente.
6. Cadastrar um fornecedor com marca.
7. Cadastrar uma caixa d'agua (escolher marca e ver fornecedor automatico).
8. Verificar que a compra gerou saida no caixa.
9. Cadastrar um servico.
10. Criar uma venda com produto e/ou servico.
11. Verificar que a venda gerou entrada no caixa.
12. Pagar salario do funcionario e verificar saldo.
13. Tentar pagar salario sem saldo (deve recusar).
14. Listar historico de movimentacoes.
15. Inativar um fornecedor e tentar cadastrar caixa com a marca dele (deve recusar).
