package br.com.dev.bibliotecaalpha.service;

import br.com.dev.bibliotecaalpha.exception.ServiceException;
import br.com.dev.bibliotecaalpha.model.Livro;
import br.com.dev.bibliotecaalpha.repository.LivroRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Serviço especializado na exportação de dados do acervo para formato CSV.
 * <p>
 * Responsável por buscar os registros na base de dados e convertê-los em um arquivo
 * de texto estruturado, utilizando a biblioteca Apache Commons CSV.
 * </p>
 *
 * @author Luccas Cabrini
 * @version 1.0
 */
@Service
public class ExportacaoService {

    private static final Logger log = LoggerFactory.getLogger(ExportacaoService.class);

    @Autowired
    private LivroRepository repository;

    /**
     * Gera um relatório completo em formato CSV contendo todos os livros cadastrados.
     * <p>
     * O arquivo é gerado com codificação UTF-8 e segue um layout padrão de colunas
     * (ID, ISBN, Título, Autores, Editora, Data). Caso o arquivo já exista, ele será sobrescrito.
     * </p>
     *
     * @param caminhoArquivo O caminho absoluto ou relativo onde o arquivo será salvo.
     * A extensão '.csv' é adicionada automaticamente se omitida.
     * @throws ServiceException Caso ocorra falha de permissão ou erro de I/O ao criar o arquivo.
     */
    public void exportarArquivoCSV(String caminhoArquivo) throws ServiceException {
        log.info("Iniciando exportação de CSV para o arquivo: {}", caminhoArquivo);

        try {

            if (!caminhoArquivo.toLowerCase().endsWith(".csv")) {
                caminhoArquivo += ".csv";
            }

            List<Livro> livros = repository.findAll();
            log.info("Total de livros encontrados para exportação: {}", livros.size());

            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(caminhoArquivo), StandardCharsets.UTF_8);
                 CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("ID", "ISBN", "Titulo", "Autores", "Editora", "Data"))) {

                for (Livro livro : livros) {
                    csvPrinter.printRecord(livro.getId(), livro.getIsbn(), livro.getTitulo(), livro.getAutores(), livro.getEditora(), livro.getDataPublicacao());
                }

                csvPrinter.flush();
            }

            log.info("Arquivo de exportação gerado com sucesso em: {}", caminhoArquivo);

        } catch (IOException e) {
            log.error("Falha ao exportar arquivo CSV", e);
            throw new ServiceException("Erro ao exportar arquivo CSV: " + e.getMessage());
        }
    }
}