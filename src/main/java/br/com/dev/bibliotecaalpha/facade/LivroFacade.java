package br.com.dev.bibliotecaalpha.facade;

import br.com.dev.bibliotecaalpha.exception.ServiceException;
import br.com.dev.bibliotecaalpha.model.Livro;
import br.com.dev.bibliotecaalpha.service.LivroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Atua como uma fachada (Facade) para o sistema de biblioteca.
 * <p>
 * Esta classe centraliza a comunicação entre a camada de apresentação (View)
 * e os serviços de negócio (Services). Ela repassa as exceções de negócio (ServiceException)
 * para serem tratadas visualmente nas telas.
 *
 * @author Luccas Cabrini
 * @version 1.0
 */
@Component
public class LivroFacade {

    private static final Logger log = LoggerFactory.getLogger(LivroFacade.class);

    @Autowired
    private LivroService livroService;

    /**
     * Solicita o salvamento de um livro (criação ou atualização).
     *
     * @param livro O objeto Livro contendo os dados a serem persistidos.
     * @throws ServiceException Caso ocorra erro de validação (regras de negócio) ou banco de dados.
     */
    public void salvarLivro(Livro livro) throws ServiceException {
        log.info("Solicitação de salvamento recebida. Título: {}, ISBN: {}", livro.getTitulo(), livro.getIsbn());
        livroService.salvar(livro);
    }

    /**
     * Recupera a lista completa de livros cadastrados.
     *
     * @return Lista contendo todos os livros da base de dados.
     */
    public List<Livro> buscarTodos() {
        log.debug("Solicitando listagem completa de livros.");
        return livroService.listarTodos();
    }

    /**
     * Solicita a exclusão de um livro pelo seu identificador.
     *
     * @param id O ID único do livro a ser excluído.
     */
    public void excluirLivro(Long id) {
        log.info("Solicitação de exclusão recebida para ID: {}", id);
        livroService.excluir(id);
    }

    /**
     * Busca um livro específico pelo seu ID.
     *
     * @param id O identificador único do livro.
     * @return O objeto Livro encontrado.
     * @throws ServiceException Caso o livro não seja encontrado.
     */
    public Livro buscarLivroPorId(Long id) throws ServiceException {
        log.debug("Buscando detalhes do livro ID: {}", id);
        return livroService.buscarPorId(id);
    }

    /**
     * Obtém o número total de livros cadastrados para exibição no Dashboard.
     *
     * @return Inteiro representando a contagem total.
     */
    public int getTotalLivros() {
        return (int) livroService.contarTotalLivros();
    }

    /**
     * Obtém o título do último livro adicionado ao acervo.
     *
     * @return String com o título do livro ou mensagem padrão caso vazio.
     */
    public String getUltimoLivro() {
        return livroService.buscarNomeUltimoLivro();
    }

    /**
     * Obtém o número total de editoras distintas cadastradas.
     *
     * @return Quantidade de editoras únicas.
     */
    public int getTotalEditoras() {
        return (int) livroService.contarEditorasUnicas();
    }

    /**
     * Realiza uma busca em uma API externa (OpenLibrary) utilizando o ISBN.
     *
     * @param isbn O código ISBN do livro.
     * @return Um objeto Livro preenchido com dados externos.
     * @throws ServiceException Caso o livro não seja encontrado ou haja erro de conexão.
     */
    public Livro buscarNaApiExterna(String isbn) throws ServiceException {
        log.info("Solicitação de busca externa para ISBN: {}", isbn);
        return livroService.buscarNaApiExterna(isbn);
    }

    /**
     * Busca a imagem da capa do livro em serviço externo.
     *
     * @param isbn O ISBN do livro.
     * @return Array de bytes da imagem ou null se não encontrada.
     */
    public byte[] buscarCapaPorIsbn(String isbn) {
        return livroService.buscarCapaPorIsbn(isbn);
    }
}