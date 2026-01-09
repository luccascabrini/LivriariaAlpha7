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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportacaoServiceTest {

    @InjectMocks
    private ImportacaoService service;

    @Mock
    private LivroRepository repository;

    @Test
    @DisplayName("Deve importar novo livro com sucesso a partir de CSV")
    void deveImportarNovoLivro(@TempDir Path tempDir) throws IOException, ServiceException {
        File arquivoCsv = tempDir.resolve("livros_novos.csv").toFile();
        try (FileWriter writer = new FileWriter(arquivoCsv)) {
            writer.write("ISBN,Titulo,Autores,Editora,Data\n");
            writer.write("978-123,Livro Teste,Autor Teste,Editora Teste,2023");
        }

        when(repository.findByIsbn("978-123")).thenReturn(Optional.empty());

        service.importarArquivoCSV(arquivoCsv.getAbsolutePath());

        verify(repository, times(1)).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve atualizar livro existente quando ISBN ja consta no banco")
    void deveAtualizarLivroExistente(@TempDir Path tempDir) throws IOException, ServiceException {
        File arquivoCsv = tempDir.resolve("livros_atualizacao.csv").toFile();
        try (FileWriter writer = new FileWriter(arquivoCsv)) {
            writer.write("ISBN,Titulo,Autores,Editora,Data\n");
            writer.write("978-456,Titulo Novo,Autor Novo,Ed Nova,2024");
        }

        Livro livroExistente = new Livro();
        livroExistente.setIsbn("978-456");
        livroExistente.setTitulo("Titulo Antigo");

        when(repository.findByIsbn("978-456")).thenReturn(Optional.of(livroExistente));

        service.importarArquivoCSV(arquivoCsv.getAbsolutePath());

        verify(repository, times(1)).save(livroExistente);
    }

    @Test
    @DisplayName("Deve lançar exceção quando arquivo não existe")
    void deveFalharArquivoInexistente() {
        assertThrows(ServiceException.class, () -> {
            service.importarArquivoCSV("caminho/que/nao/existe.csv");
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando CSV não tem coluna ISBN")
    void deveFalharCsvSemIsbn(@TempDir Path tempDir) throws IOException {
        File arquivoRuim = tempDir.resolve("sem_isbn.csv").toFile();
        try (FileWriter writer = new FileWriter(arquivoRuim)) {
            writer.write("Titulo,Autores\n");
            writer.write("Livro Sem ISBN,Autor X");
        }

        assertThrows(ServiceException.class, () -> {
            service.importarArquivoCSV(arquivoRuim.getAbsolutePath());
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando o arquivo CSV está vazio")
    void deveFalharArquivoVazio(@TempDir Path tempDir) throws IOException {
        File arquivoVazio = tempDir.resolve("vazio.csv").toFile();
        try (FileWriter writer = new FileWriter(arquivoVazio)) {
            writer.write("");
        }

        assertThrows(ServiceException.class, () -> {
            service.importarArquivoCSV(arquivoVazio.getAbsolutePath());
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando o arquivo está corrompido ou formato inválido")
    void deveFalharArquivoCorrompido(@TempDir Path tempDir) throws IOException {
        File arquivoLixo = tempDir.resolve("corrompido.csv").toFile();
        try (FileWriter writer = new FileWriter(arquivoLixo)) {
            writer.write("Isto não é um CSV válido @#$$%");
        }

        assertThrows(ServiceException.class, () -> {
            service.importarArquivoCSV(arquivoLixo.getAbsolutePath());
        });
    }
}