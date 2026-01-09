# Biblioteca Alpha

Projeto de avaliação focado no desenvolvimento de um sistema de gestão de acervo bibliográfico, utilizando Java 8 e Spring Boot com interface gráfica Swing.

## 📋 Sobre o Projeto

Esta aplicação desktop permite o controle de livros de uma biblioteca. Diferente de aplicações Swing tradicionais, este projeto utiliza o **Spring Boot** para gerenciar a infraestrutura (injeção de dependências, conexão com banco) e o **FlatLaf** para modernizar a aparência visual.

## 🛠️ Stack Tecnológica

O projeto foi construído com base nas seguintes definições do `pom.xml`:

### Core
* **Java 8** (JDK 1.8) - Linguagem base.
* **Spring Boot 2.7.18** - Framework para configuração automática e gestão de contexto.
* **Maven** - Gerenciamento de dependências e build.

### Interface Gráfica (GUI)
* **Java Swing** - Biblioteca nativa de interfaces.
* **FlatLaf 3.2.5** - Look and Feel (Tema) para modernizar o Swing (Dark/Light mode).

### Dados & Persistência
* **PostgreSQL** - Banco de dados relacional.
* **Spring Data JPA** - Camada de persistência e ORM (Hibernate).
* **Spring Boot Validation** - Validação de dados de entrada.

### Utilitários & Integrações
* **OkHttp 4.12.0** - Cliente HTTP para requisições externas.
* **Apache Commons CSV 1.10.0** - Leitura e escrita de arquivos CSV (Relatórios/Importação).
* **Gson** e **JSON (org.json)** - Serialização e manipulação de objetos JSON.

## 🚀 Como Executar

### Pré-requisitos
* Java JDK 8 instalado.
* Maven instalado.
* PostgreSQL rodando (Banco de dados configurado no `application.properties`).

### Passo a passo

1.  **Clone o repositório:**
    ```bash
    git clone [(https://github.com/luccascabrini/LivriariaAlpha7.git)]
    ```

2.  **Instale as dependências:**
    ```bash
    mvn clean install
    ```

3.  **Execute a aplicação:**
    ```bash
    mvn spring-boot:run
    ```

## 📦 Estrutura do Projeto

* `br.com.dev.bibliotecaalpha` - Pacote raiz.
* A aplicação utiliza o padrão MVC (adaptado para Desktop) ou arquitetura em camadas (Controller/Service/Repository).

---
**Desenvolvido por Luccas Cabrini**
