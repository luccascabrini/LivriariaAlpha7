package br.com.dev.bibliotecaalpha.service;

import br.com.dev.bibliotecaalpha.exception.ServiceException;
import br.com.dev.bibliotecaalpha.model.Livro;
import br.com.dev.bibliotecaalpha.repository.LivroRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportacaoServiceTest {

    @InjectMocks
    private ExportacaoService service;

    @Mock
    private LivroRepository repository;

    @Test
    @DisplayName("Deve exportar livros para arquivo CSV com sucesso")
    void deveExportarCsvComSucesso(@TempDir Path tempDir) throws ServiceException, IOException {
        File arquivoSaida = tempDir.resolve("relatorio_teste.csv").toFile();

        Livro l1 = new Livro();
        l1.setId(1L);
        l1.setIsbn("111");
        l1.setTitulo("Livro Exportado");
        l1.setAutores("Autor Exp");
        l1.setEditora("Ed Exp");
        l1.setDataPublicacao("2023");

        when(repository.findAll()).thenReturn(Arrays.asList(l1));

        service.exportarArquivoCSV(arquivoSaida.getAbsolutePath());

        assertTrue(arquivoSaida.exists());

        List<String> linhas = Files.readAllLines(arquivoSaida.toPath());
        assertTrue(linhas.size() >= 2);
        assertTrue(linhas.get(1).contains("Livro Exportado"));
    }

    @Test
    @DisplayName("Deve gerar CSV apenas com cabeçalho quando não há livros")
    void deveGerarCsvVazio(@TempDir Path tempDir) throws ServiceException, IOException {
        File arquivoSaida = tempDir.resolve("vazio.csv").toFile();

        when(repository.findAll()).thenReturn(Collections.emptyList());

        service.exportarArquivoCSV(arquivoSaida.getAbsolutePath());

        assertTrue(arquivoSaida.exists());
        List<String> linhas = Files.readAllLines(arquivoSaida.toPath());
        assertTrue(linhas.size() >= 1);
        assertTrue(linhas.get(0).contains("ISBN"));
    }

    @Test
    @DisplayName("Deve gerar CSV contendo apenas o cabeçalho quando não houver livros")
    void deveGerarCsvVazio_QuandoBancoVazio(@TempDir Path tempDir) throws IOException, ServiceException {

        when(repository.findAll()).thenReturn(Collections.emptyList());

        File arquivoSaida = tempDir.resolve("relatorio_vazio.csv").toFile();

        service.exportarArquivoCSV(arquivoSaida.getAbsolutePath());

        assertTrue(arquivoSaida.exists());
        List<String> linhas = Files.readAllLines(arquivoSaida.toPath());

        assertEquals(1, linhas.size());

        assertEquals("ID,ISBN,Titulo,Autores,Editora,Data", linhas.get(0));
    }
}