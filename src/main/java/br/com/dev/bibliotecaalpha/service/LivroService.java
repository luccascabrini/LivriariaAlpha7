package br.com.dev.bibliotecaalpha.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.dev.bibliotecaalpha.exception.ServiceException;
import br.com.dev.bibliotecaalpha.model.Livro;
import br.com.dev.bibliotecaalpha.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Classe de Serviço responsável pelas Regras de Negócio da entidade Livro.
 * <p>
 * Centraliza as operações de validação, persistência, exclusão e consultas
 * especializadas, servindo como intermediária entre o Controller/Facade e o Repositório.
 * </p>
 *
 * @author Luccas Cabrini
 * @version 1.0
 */
@Service
public class LivroService {

    private static final Logger log = LoggerFactory.getLogger(LivroService.class);

    @Autowired
    private LivroRepository repository;

    @Autowired
    private OpenLibraryService openLibraryService;

    /**
     * Salva ou atualiza um livro no banco de dados.
     * <p>
     * Antes de persistir, realiza as seguintes validações:
     * <ul>
     * <li>Verifica campos obrigatórios (Título, ISBN, Autores, Data).</li>
     * <li>Verifica duplicidade de ISBN para evitar dois livros diferentes com o mesmo código.</li>
     * </ul>
     * </p>
     *
     * @param livro O objeto {@link Livro} a ser salvo.
     * @throws ServiceException Caso alguma regra de validação seja violada ou ocorra erro no banco.
     */
    @Transactional
    public void salvar(Livro livro) throws ServiceException {
        log.info("Tentando salvar livro. Título: '{}', ISBN: '{}'", livro.getTitulo(), livro.getIsbn());

        validarCamposObrigatorios(livro);

        Optional<Livro> existente = repository.findByIsbn(livro.getIsbn());
        if (existente.isPresent()) {
            if (livro.getId() == null || !livro.getId().equals(existente.get().getId())) {
                log.warn("Tentativa de cadastro duplicado para ISBN: {}", livro.getIsbn());
                throw new ServiceException("Já existe um livro cadastrado com este ISBN: " + livro.getIsbn());
            }
        }

        try {
            Livro salvo = repository.save(livro);
            log.info("Livro salvo com sucesso. ID: {}", salvo.getId());
        } catch (Exception e) {
            log.error("Erro ao persistir livro no banco de dados", e);
            throw new ServiceException("Erro técnico ao salvar livro: " + e.getMessage());
        }
    }

    /**
     * Retorna a lista completa de livros cadastrados.
     *
     * @return Lista de objetos {@link Livro}.
     */
    public List<Livro> listarTodos() {
        log.debug("Listando todos os livros do acervo");
        return repository.findAll();
    }

    /**
     * Remove um livro permanentemente do banco de dados.
     *
     * @param id O identificador único do livro a ser excluído.
     */
    @Transactional
    public void excluir(Long id) {
        log.info("Solicitação de exclusão para o livro ID: {}", id);
        repository.deleteById(id);
        log.info("Livro ID {} excluído com sucesso", id);
    }

    /**
     * Busca um livro pelo seu ID.
     *
     * @param id O identificador do livro.
     * @return O livro encontrado.
     * @throws ServiceException Caso nenhum livro seja encontrado com o ID informado.
     */
    public Livro buscarPorId(Long id) throws ServiceException {
        return repository.findById(id).orElseThrow(() -> {
            log.warn("Livro não encontrado para ID: {}", id);
            return new ServiceException("Livro não encontrado com o ID: " + id);
        });
    }

    /**
     * Conta o número total de livros no acervo.
     * Utilizado para alimentar indicadores no Dashboard.
     *
     * @return Quantidade total de registros.
     */
    public long contarTotalLivros() {
        return repository.count();
    }

    /**
     * Recupera o título do último livro adicionado ao acervo.
     *
     * @return String contendo o título do livro ou "Nenhum livro" caso a base esteja vazia.
     */
    public String buscarNomeUltimoLivro() {

        List<Livro> livros = repository.findAll();

        if (livros.isEmpty()) {
            return "Nenhum livro";
        }

        Livro ultimo = livros.get(livros.size() - 1);
        return ultimo.getTitulo();
    }

    /**
     * Calcula a quantidade de editoras distintas presentes no acervo.
     * <p>
     * Realiza uma filtragem em memória para ignorar campos vazios ou nulos.
     * </p>
     *
     * @return Número de editoras únicas.
     */
    public long contarEditorasUnicas() {

        List<Livro> todos = repository.findAll();

        if (todos.isEmpty()) {
            return 0;
        }

        return todos.stream().map(Livro::getEditora).filter(editora -> editora != null && !editora.isEmpty()).distinct().count();
    }

    /**
     * Consulta a API externa (OpenLibrary) para buscar dados de um livro pelo ISBN.
     *
     * @param isbn O ISBN a ser pesquisado.
     * @return Um objeto Livro preenchido com os dados da API.
     * @throws ServiceException Caso o livro não seja encontrado na API ou ocorra erro de conexão.
     */
    public Livro buscarNaApiExterna(String isbn) throws ServiceException {
        log.info("Iniciando consulta externa na OpenLibrary para ISBN: {}", isbn);
        try {

            Livro livro = openLibraryService.buscarLivroCompleto(isbn);

            if (livro == null) {
                log.warn("API externa não retornou dados para ISBN: {}", isbn);
                throw new ServiceException("Livro não encontrado na base de dados externa.");
            }

            log.info("Livro encontrado na API externa: {}", livro.getTitulo());
            return livro;

        } catch (Exception e) {
            log.error("Falha na comunicação com API externa", e);
            if (e instanceof ServiceException) throw (ServiceException) e;
            throw new ServiceException("Falha na consulta externa: " + e.getMessage());
        }
    }

    /**
     * Busca a imagem da capa de um livro na API externa.
     *
     * @param isbn O ISBN do livro.
     * @return Array de bytes da imagem (BLOB).
     */
    public byte[] buscarCapaPorIsbn(String isbn) {
        log.debug("Baixando capa para ISBN: {}", isbn);
        return openLibraryService.baixarCapa(isbn);
    }

    /**
     * Método auxiliar para validar se os campos obrigatórios foram preenchidos.
     *
     * @param livro O livro a ser validado.
     * @throws ServiceException Se algum campo obrigatório estiver vazio ou nulo.
     */
    private void validarCamposObrigatorios(Livro livro) throws ServiceException {
        if (livro.getTitulo() == null || livro.getTitulo().trim().isEmpty()) {
            log.warn("Validação falhou: Título vazio");
            throw new ServiceException("O Título é obrigatório.");
        }
        if (livro.getIsbn() == null || livro.getIsbn().trim().isEmpty()) {
            log.warn("Validação falhou: ISBN vazio");
            throw new ServiceException("O ISBN é obrigatório.");
        }
        if (livro.getAutores() == null || livro.getAutores().trim().isEmpty()) {
            log.warn("Validação falhou: Autores vazio");
            throw new ServiceException("O campo Autor(es) é obrigatório.");
        }
        if (livro.getDataPublicacao() == null || livro.getDataPublicacao().trim().isEmpty()) {
            log.warn("Validação falhou: Data Publicação vazia");
            throw new ServiceException("A Data de Publicação é obrigatória.");
        }
    }
}