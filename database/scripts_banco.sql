-- ============================================================================
-- SCRIPT DE CRIAÇÃO E CARGA INICIAL - BIBLIOTECA ALPHA
-- Banco de Dados: PostgreSQL
-- Autor: Luccas Cabrini
-- Data: 2026
-- ============================================================================

DROP TABLE IF EXISTS livro;

-- 2. Criação da Tabela
-- Observação: A estrutura reflete a entidade JPA 'Livro.java'
CREATE TABLE livro (
    id BIGSERIAL PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    titulo VARCHAR(255) NOT NULL,
    autores VARCHAR(255) NOT NULL,
    editora VARCHAR(255),
    data_publicacao VARCHAR(50),
    livros_semelhantes TEXT,
    capa_imagem BYTEA
);

-- 3. Carga Inicial de Dados

INSERT INTO livro (titulo, isbn, autores, editora, data_publicacao, livros_semelhantes)
VALUES ('Clean Code: A Handbook of Agile Software Craftsmanship', '9780132350884', 'Robert C. Martin', 'Prentice Hall', '2008', 'The Clean Coder, Refactoring');

INSERT INTO livro (titulo, isbn, autores, editora, data_publicacao, livros_semelhantes)
VALUES ('Harry Potter e a Pedra Filosofal', '9788532511010', 'J.K. Rowling', 'Rocco', '2000', 'Percy Jackson, As Crônicas de Nárnia');

INSERT INTO livro (titulo, isbn, autores, editora, data_publicacao, livros_semelhantes)
VALUES ('Domain-Driven Design', '9780321125217', 'Eric Evans', 'Addison-Wesley', '2003', 'Patterns of Enterprise Application Architecture');

INSERT INTO livro (titulo, isbn, autores, editora, data_publicacao, livros_semelhantes)
VALUES ('O Senhor dos Anéis: A Sociedade do Anel', '9788595084742', 'J.R.R. Tolkien', 'HarperCollins', '1954', 'O Hobbit, O Silmarillion');

INSERT INTO livro (titulo, isbn, autores, editora, data_publicacao, livros_semelhantes)
VALUES ('Effective Java', '9780134685991', 'Joshua Bloch', 'Addison-Wesley', '2017', 'Java Concurrency in Practice');

