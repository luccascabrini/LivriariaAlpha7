package br.com.dev.bibliotecaalpha.service;

import br.com.dev.bibliotecaalpha.model.Livro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class OpenLibraryServiceTest {

    @Spy
    private OpenLibraryService service;

    private final String JSON_SUCESSO = "{"
            + "\"ISBN:9780545010221\": {"
            + "  \"title\": \"Harry Potter and the Deathly Hallows\","
            + "  \"publish_date\": \"July 21, 2007\","
            + "  \"authors\": [{ \"name\": \"J. K. Rowling\" }],"
            + "  \"publishers\": [{ \"name\": \"Arthur A. Levine Books\" }]"
            + "}}";

    private final String JSON_VAZIO = "{}";

    @BeforeEach
    void setup() {
    }

    @Test
    @DisplayName("Deve retornar livro preenchido quando a API responder JSON válido")
    void deveRetornarLivroCompleto_QuandoJsonValido() throws Exception {
        doReturn(JSON_SUCESSO).when(service).fazerRequisicaoHttp(anyString());
        doReturn(null).when(service).baixarCapa(anyString());

        Livro resultado = service.buscarLivroCompleto("9780545010221");

        assertNotNull(resultado);
        assertEquals("Harry Potter and the Deathly Hallows", resultado.getTitulo());
        assertEquals("J. K. Rowling", resultado.getAutores());
        assertEquals("Arthur A. Levine Books", resultado.getEditora());
        assertEquals("2007", resultado.getDataPublicacao());
    }

    @Test
    @DisplayName("Deve retornar NULL quando a API responder JSON vazio ou livro não encontrado")
    void deveRetornarNull_QuandoLivroNaoEncontrado() throws Exception {
        doReturn(JSON_VAZIO).when(service).fazerRequisicaoHttp(anyString());

        Livro resultado = service.buscarLivroCompleto("12345");

        assertNull(resultado, "Deveria retornar null para JSON vazio");
    }

    @Test
    @DisplayName("Deve realizar download da capa se bytes forem validos")
    void deveBaixarCapa_QuandoImagemValida() throws Exception {
        byte[] imagemFake = new byte[150];
        InputStream streamFake = new ByteArrayInputStream(imagemFake);

        doReturn(streamFake).when(service).abrirStreamUrl(anyString());

        byte[] resultado = service.baixarCapa("9780545010221");

        assertNotNull(resultado);
        assertEquals(150, resultado.length);
    }

    @Test
    @DisplayName("Deve ignorar capa se a imagem for muito pequena (pixel transparente)")
    void deveIgnorarCapa_QuandoImagemPequena() throws Exception {
        byte[] imagemPequena = new byte[50];
        InputStream streamFake = new ByteArrayInputStream(imagemPequena);

        doReturn(streamFake).when(service).abrirStreamUrl(anyString());

        byte[] resultado = service.baixarCapa("9780545010221");

        assertNull(resultado);
    }
}