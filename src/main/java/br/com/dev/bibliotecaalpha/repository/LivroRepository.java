package br.com.dev.bibliotecaalpha.repository;

import br.com.dev.bibliotecaalpha.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface de repositório para acesso a dados da entidade {@link Livro}.
 * <p>
 * Estende {@link JpaRepository}, herdando operações de CRUD padrão e paginação.
 * </p>
 *
 * @author Luccas Cabrini
 * @version 1.0
 */
@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    /**
     * Busca um livro exato pelo seu código ISBN.
     * Utilizado para validar duplicidade no cadastro e na importação.
     *
     * @param isbn O código ISBN a ser pesquisado.
     * @return Um {@link Optional} contendo o livro caso encontrado, ou vazio caso contrário.
     */
    Optional<Livro> findByIsbn(String isbn);

}