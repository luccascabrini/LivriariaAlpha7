package br.com.dev.bibliotecaalpha.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Representa a entidade Livro no sistema da biblioteca.
 * <p>
 * Esta classe é mapeada para a tabela 'livro' no banco de dados e contém
 * todas as validações de integridade dos dados (JPA e Bean Validation).
 * </p>
 *
 * @author Luccas Cabrini
 * @version 1.0
 */
@Entity
@Table(name = "livro")
public class Livro implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Título do livro. Campo obrigatório.
     */
    @NotBlank(message = "O título do livro não pode estar vazio.")
    @Size(max = 255, message = "O título deve ter no máximo 255 caracteres.")
    @Column(name = "titulo", nullable = false)
    private String titulo;

    /**
     * Nome dos autores. Campo obrigatório.
     */
    @NotBlank(message = "O autor é obrigatório.")
    @Column(name = "autores", nullable = false)
    private String autores;

    /**
     * Data de publicação em formato texto.
     */
    @Column(name = "data_publicacao")
    private String dataPublicacao;

    /**
     * Código ISBN (International Standard Book Number).
     * Deve ser único no banco de dados.
     */
    @NotBlank(message = "O ISBN é obrigatório.")
    @Size(max = 20, message = "O ISBN deve ter no máximo 20 caracteres.")
    @Column(name = "isbn", unique = true, nullable = false)
    private String isbn;

    /**
     * Editora responsável pela publicação.
     */
    @Column(name = "editora")
    private String editora;

    /**
     * Texto contendo referências a livros semelhantes ou observações.
     * Mapeado como TEXT/CLOB no banco para suportar textos longos.
     */
    @Column(name = "livros_semelhantes", columnDefinition = "TEXT")
    private String livrosSemelhantes;

    /**
     * Imagem da capa do livro armazenada em formato binário (BLOB).
     */
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "capa_imagem")
    private byte[] capaImagem;

    /**
     * Construtor padrão necessário para o JPA.
     */
    public Livro() {
    }

    /**
     * Construtor utilitário para criação rápida de objetos.
     *
     * @param titulo Título do livro.
     * @param isbn   Código ISBN.
     */
    public Livro(String titulo, String isbn) {
        this.titulo = titulo;
        this.isbn = isbn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutores() {
        return autores;
    }

    public void setAutores(String autores) {
        this.autores = autores;
    }

    public String getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(String dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getEditora() {
        return editora;
    }

    public void setEditora(String editora) {
        this.editora = editora;
    }

    public String getLivrosSemelhantes() {
        return livrosSemelhantes;
    }

    public void setLivrosSemelhantes(String livrosSemelhantes) {
        this.livrosSemelhantes = livrosSemelhantes;
    }

    public byte[] getCapaImagem() {
        return capaImagem;
    }

    public void setCapaImagem(byte[] capaImagem) {
        this.capaImagem = capaImagem;
    }

    @Override
    public String toString() {
        return "Livro [id=" + id + ", titulo=" + titulo + ", isbn=" + isbn + "]";
    }
}