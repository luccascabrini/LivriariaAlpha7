package br.com.dev.bibliotecaalpha.service;

import br.com.dev.bibliotecaalpha.exception.ServiceException;
import br.com.dev.bibliotecaalpha.model.Livro;
import br.com.dev.bibliotecaalpha.repository.LivroRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @InjectMocks
    private LivroService service;

    @Mock
    private LivroRepository repository;

    @Mock
    private OpenLibraryService openLibraryService;

    @Test
    @DisplayName("Deve salvar livro com sucesso quando dados são válidos")
    void deveSalvarLivro_ComSucesso() throws ServiceException {
        Livro livro = criarLivroValido();

        when(repository.findByIsbn(livro.getIsbn())).thenReturn(Optional.empty());
        when(repository.save(livro)).thenReturn(livro);

        assertDoesNotThrow(() -> service.salvar(livro));

        verify(repository, times(1)).save(livro);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar salvar ISBN duplicado")
    void deveLancarErro_IsbnDuplicado() {
        Livro novoLivro = criarLivroValido();
        novoLivro.setId(null);

        Livro livroExistente = criarLivroValido();
        livroExistente.setId(10L);

        when(repository.findByIsbn(novoLivro.getIsbn())).thenReturn(Optional.of(livroExistente));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.salvar(novoLivro));
        assertTrue(ex.getMessage().contains("Já existe um livro cadastrado com este ISBN"));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção de validação para Título vazio")
    void deveLancarErro_TituloVazio() {
        Livro livro = criarLivroValido();
        livro.setTitulo("");

        ServiceException ex = assertThrows(ServiceException.class, () -> service.salvar(livro));
        assertEquals("O Título é obrigatório.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve calcular corretamente editoras únicas")
    void deveContarEditorasUnicas() {
        Livro l1 = new Livro(); l1.setEditora("Rocco");
        Livro l2 = new Livro(); l2.setEditora("Arqueiro");
        Livro l3 = new Livro(); l3.setEditora("Rocco");

        when(repository.findAll()).thenReturn(Arrays.asList(l1, l2, l3));

        long total = service.contarEditorasUnicas();

        assertEquals(2, total);
    }

    @Test
    @DisplayName("Deve buscar o nome do último livro da lista")
    void deveBuscarNomeUltimoLivro() {
        Livro l1 = new Livro(); l1.setTitulo("Livro A");
        Livro l2 = new Livro(); l2.setTitulo("Livro B");

        when(repository.findAll()).thenReturn(Arrays.asList(l1, l2));

        String nome = service.buscarNomeUltimoLivro();

        assertEquals("Livro B", nome);
    }

    @Test
    @DisplayName("Deve chamar API externa ao buscar por ISBN")
    void deveBuscarNaApiExterna() throws ServiceException {
        String isbn = "123";
        Livro livroApi = new Livro();
        livroApi.setTitulo("Livro API");

        when(openLibraryService.buscarLivroCompleto(isbn)).thenReturn(livroApi);

        Livro resultado = service.buscarNaApiExterna(isbn);

        assertNotNull(resultado);
        assertEquals("Livro API", resultado.getTitulo());
        verify(openLibraryService).buscarLivroCompleto(isbn);
    }

    @Test
    @DisplayName("Deve lançar erro técnico quando o banco de dados falhar ao salvar")
    void deveLancarErroTecnico_QuandoBancoFalhar() {
        Livro livro = criarLivroValido();

        when(repository.findByIsbn(livro.getIsbn())).thenReturn(Optional.empty());
        when(repository.save(any(Livro.class))).thenThrow(new RuntimeException("Erro de conexão JDBC"));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.salvar(livro));

        assertTrue(ex.getMessage().contains("Erro técnico ao salvar livro"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando buscar um ID que não existe")
    void deveLancarErro_QuandoIdNaoEncontrado() {
        Long idInexistente = 99L;
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        ServiceException ex = assertThrows(ServiceException.class, () -> service.buscarPorId(idInexistente));

        assertEquals("Livro não encontrado com o ID: 99", ex.getMessage());
    }

    @Test
    @DisplayName("Deve relançar exceção quando a API externa falhar")
    void deveTratarErro_QuandoApiExternaFalhar() throws ServiceException {
        String isbn = "99999";
        when(openLibraryService.buscarLivroCompleto(isbn))
                .thenThrow(new ServiceException("Erro de conexão com OpenLibrary"));

        ServiceException ex = assertThrows(ServiceException.class, () -> service.buscarNaApiExterna(isbn));

        assertTrue(ex.getMessage().contains("Erro de conexão com OpenLibrary"));
    }

    @Test
    @DisplayName("Deve lançar erro quando a API retornar NULL")
    void deveLancarErro_QuandoApiRetornarNull() throws ServiceException {
        String isbn = "88888";
        when(openLibraryService.buscarLivroCompleto(isbn)).thenReturn(null);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.buscarNaApiExterna(isbn));

        assertEquals("Livro não encontrado na base de dados externa.", ex.getMessage());
    }

    private Livro criarLivroValido() {
        Livro l = new Livro();
        l.setTitulo("Clean Code");
        l.setIsbn("9780132350884");
        l.setAutores("Robert C. Martin");
        l.setEditora("Prentice Hall");
        l.setDataPublicacao("2008");
        return l;
    }
}