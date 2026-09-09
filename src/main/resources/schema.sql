-- Script de criacao do banco de dados do sistema de gerenciamento de caixas d'agua.
-- Banco: PostgreSQL

-- Tabela de pessoas: funcionarios, clientes e fornecedores compartilham essa estrutura base.
CREATE TABLE IF NOT EXISTS PESSOA (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf_cnpj VARCHAR(14) UNIQUE NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE
);

-- Tabela especifica de funcionarios.
CREATE TABLE IF NOT EXISTS FUNCIONARIO (
    pessoa_id INT PRIMARY KEY,
    salario DECIMAL(10, 2) NOT NULL,
    setor VARCHAR(50) NOT NULL,
    CONSTRAINT fk_funcionario_pessoa FOREIGN KEY (pessoa_id) REFERENCES PESSOA(id) ON DELETE CASCADE
);

-- Tabela especifica de clientes.
CREATE TABLE IF NOT EXISTS CLIENTE (
    pessoa_id INT PRIMARY KEY,
    limite_credito DECIMAL(10, 2) DEFAULT 0.0,
    CONSTRAINT fk_cliente_pessoa FOREIGN KEY (pessoa_id) REFERENCES PESSOA(id) ON DELETE CASCADE
);

-- Tabela especifica de fornecedores.
CREATE TABLE IF NOT EXISTS FORNECEDOR (
    pessoa_id INT PRIMARY KEY,
    razao_social VARCHAR(255) NOT NULL,
    marca VARCHAR(50),
    CONSTRAINT fk_fornecedor_pessoa FOREIGN KEY (pessoa_id) REFERENCES PESSOA(id) ON DELETE CASCADE
);

-- Tabela de caixas d'agua em estoque.
CREATE TABLE IF NOT EXISTS CAIXA_DA_AGUA (
    id SERIAL PRIMARY KEY,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(255) NOT NULL,
    capacidade_litros INT NOT NULL,
    largura DECIMAL(10, 2) NOT NULL,
    altura DECIMAL(10, 2) NOT NULL,
    profundidade DECIMAL(10, 2) NOT NULL,
    cor VARCHAR(50) NOT NULL,
    material VARCHAR(50) NOT NULL,
    formato VARCHAR(50) NOT NULL,
    preco DECIMAL(10, 2) NOT NULL,
    quantidade INT NOT NULL,
    fornecedor_id INT NOT NULL,
    CONSTRAINT fk_caixa_fornecedor FOREIGN KEY (fornecedor_id) REFERENCES PESSOA(id)
);

-- Tabela de servicos oferecidos.
CREATE TABLE IF NOT EXISTS SERVICO (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    preco DECIMAL(10, 2) NOT NULL
);

-- Tabela de vendas (cabecalho).
CREATE TABLE IF NOT EXISTS VENDA (
    id SERIAL PRIMARY KEY,
    funcionario_id INT NOT NULL,
    cliente_id INT NOT NULL,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valor_total DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_venda_funcionario FOREIGN KEY (funcionario_id) REFERENCES PESSOA(id),
    CONSTRAINT fk_venda_cliente FOREIGN KEY (cliente_id) REFERENCES PESSOA(id)
);

-- Tabela de itens de venda: relaciona vendas com produtos ou servicos.
CREATE TABLE IF NOT EXISTS VENDA_ITEM (
    id SERIAL PRIMARY KEY,
    venda_id INT NOT NULL,
    caixa_da_agua_id INT,
    servico_id INT,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL,
    valor_total DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_item_venda FOREIGN KEY (venda_id) REFERENCES VENDA(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_caixa FOREIGN KEY (caixa_da_agua_id) REFERENCES CAIXA_DA_AGUA(id),
    CONSTRAINT fk_item_servico FOREIGN KEY (servico_id) REFERENCES SERVICO(id)
);

-- Tabela de movimentacoes financeiras (fluxo de caixa).
CREATE TABLE IF NOT EXISTS MOVIMENTACAO_FINANCEIRA (
    id SERIAL PRIMARY KEY,
    valor DECIMAL(10, 2) NOT NULL,
    pagador VARCHAR(255) NOT NULL,
    recebedor VARCHAR(255) NOT NULL,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    motivo TEXT NOT NULL,
    responsavel VARCHAR(255) NOT NULL,
    tipo VARCHAR(20) NOT NULL
);

-- Tabela de logs do sistema para auditoria.
CREATE TABLE IF NOT EXISTS LOG_SISTEMA (
    id SERIAL PRIMARY KEY,
    nivel VARCHAR(10) NOT NULL,
    mensagem TEXT NOT NULL,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
