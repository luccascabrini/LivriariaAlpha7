package br.com.dev.bibliotecaalpha.service;

import br.com.dev.bibliotecaalpha.exception.ServiceException;
import br.com.dev.bibliotecaalpha.model.Livro;
import br.com.dev.bibliotecaalpha.repository.LivroRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Serviço responsável pela importação em lote de livros através de arquivos CSV.
 * <p>
 * Processa arquivos contendo metadados de livros (ISBN, Título, Autores, Editora, Data)
 * e atualiza a base de dados. Se o ISBN já existir, o registro é atualizado;
 * caso contrário, um novo livro é criado.
 * </p>
 *
 * @author Luccas Cabrini
 * @version 1.1
 */
@Service
public class ImportacaoService {

    private static final Logger log = LoggerFactory.getLogger(ImportacaoService.class);

    @Autowired
    private LivroRepository repository;

    /**
     * Lê um arquivo CSV do sistema de arquivos e persiste os dados na base.
     * <p>
     * O arquivo deve conter um cabeçalho obrigatório com as colunas:
     * <ul>
     * <li>ISBN (Chave única)</li>
     * <li>Titulo</li>
     * <li>Autores</li>
     * <li>Editora</li>
     * <li>Data</li>
     * </ul>
     * </p>
     *
     * @param caminhoArquivo O caminho absoluto ou relativo para o arquivo CSV.
     * @throws ServiceException Se o arquivo não existir, estiver vazio, tiver formato inválido
     * (faltando coluna ISBN) ou ocorrer erro de leitura (I/O).
     */
    @Transactional
    public void importarArquivoCSV(String caminhoArquivo) throws ServiceException {
        log.info("Iniciando processo de importação CSV. Arquivo: {}", caminhoArquivo);

        if (!Files.exists(Paths.get(caminhoArquivo))) {
            log.error("Arquivo CSV não encontrado no caminho: {}", caminhoArquivo);
            throw new ServiceException("Arquivo não encontrado: " + caminhoArquivo);
        }

        try (Reader reader = Files.newBufferedReader(Paths.get(caminhoArquivo));
             CSVParser csvParser = CSVFormat.DEFAULT
                     .withHeader()
                     .withSkipHeaderRecord()
                     .withTrim()
                     .parse(reader)) {

            if (csvParser.getHeaderMap() == null || csvParser.getHeaderMap().isEmpty()) {
                throw new ServiceException("O arquivo CSV está vazio ou com formato inválido.");
            }

            if (!csvParser.getHeaderMap().containsKey("ISBN")) {
                log.error("Estrutura do CSV inválida: Coluna ISBN ausente.");
                throw new ServiceException("O arquivo CSV está inválido: Coluna 'ISBN' não encontrada.");
            }

            int novos = 0;
            int atualizados = 0;

            for (CSVRecord csvRecord : csvParser) {
                String isbn = csvRecord.get("ISBN");
                String titulo = csvRecord.get("Titulo");
                String autores = csvRecord.get("Autores");
                String editora = csvRecord.get("Editora");
                String data = csvRecord.get("Data");

                Optional<Livro> existente = repository.findByIsbn(isbn);
                Livro livro;

                if (existente.isPresent()) {
                    log.debug("Atualizando livro existente. ISBN: {}", isbn);
                    livro = existente.get();
                    atualizados++;
                } else {
                    log.debug("Criando novo livro. ISBN: {}", isbn);
                    livro = new Livro();
                    livro.setIsbn(isbn);
                    novos++;
                }

                livro.setTitulo(titulo);
                livro.setAutores(autores);
                livro.setEditora(editora);
                livro.setDataPublicacao(data);

                repository.save(livro);
            }

            log.info("Importação finalizada com sucesso. Total lido: {}, Novos: {}, Atualizados: {}",
                    csvParser.getRecordNumber(), novos, atualizados);

        } catch (IOException e) {
            log.error("Erro de IO ao processar arquivo", e);
            throw new ServiceException("Erro ao ler arquivo CSV: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Erro de formato no CSV", e);
            throw new ServiceException("Formato do CSV inválido: " + e.getMessage());
        }
    }
}