package br.com.dev.bibliotecaalpha.service;

import br.com.dev.bibliotecaalpha.exception.ServiceException;
import br.com.dev.bibliotecaalpha.model.Livro;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Serviço de integração responsável pela comunicação com a API pública da Open Library.
 * <p>
 * Esta classe realiza requisições HTTP para buscar metadados de livros (Título, Autor, Editora, Data)
 * e fazer o download de capas, utilizando o ISBN como chave de busca.
 * </p>
 *
 * @author Luccas Cabrini
 * @version 1.0
 */
@Service
public class OpenLibraryService {

    private static final Logger log = LoggerFactory.getLogger(OpenLibraryService.class);

    /**
     * URL base da API de dados da Open Library. O '%s' será substituído pelo ISBN.
     */
    private static final String API_URL = "https://openlibrary.org/api/books?bibkeys=ISBN:%s&jscmd=data&format=json";

    /**
     * Busca os dados completos de um livro a partir do ISBN.
     * <p>
     * O método realiza o parse do JSON retornado pela API, extraindo:
     * <ul>
     * <li>Título</li>
     * <li>Primeiro Autor da lista</li>
     * <li>Primeira Editora da lista</li>
     * <li>Ano de publicação (extraído via Regex da data completa)</li>
     * </ul>
     * Também aciona o download da imagem da capa, se disponível.
     * </p>
     *
     * @param isbn O código ISBN do livro a ser consultado.
     * @return Um objeto {@link Livro} preenchido, ou {@code null} se o livro não for encontrado na API.
     * @throws ServiceException Caso ocorra erro de conexão ou falha ao processar o JSON.
     */
    public Livro buscarLivroCompleto(String isbn) throws ServiceException {
        log.info("Consultando OpenLibrary API para ISBN: {}", isbn);
        try {
            String jsonResposta = fazerRequisicaoHttp(String.format(API_URL, isbn));

            if (jsonResposta == null || jsonResposta.equals("{}")) {
                log.warn("API retornou resposta vazia para o ISBN: {}", isbn);
                return null;
            }

            JSONObject jsonRoot = new JSONObject(jsonResposta);
            String chaveIsbn = "ISBN:" + isbn;

            if (!jsonRoot.has(chaveIsbn)) {
                log.warn("Chave ISBN não encontrada no JSON de resposta.");
                return null;
            }

            JSONObject dados = jsonRoot.getJSONObject(chaveIsbn);

            Livro livro = new Livro();
            livro.setIsbn(isbn);
            livro.setTitulo(dados.optString("title", "Título Desconhecido"));

            if (dados.has("authors")) {
                JSONArray autoresArray = dados.getJSONArray("authors");
                livro.setAutores(autoresArray.getJSONObject(0).getString("name"));
            } else {
                livro.setAutores("Autor Desconhecido");
            }

            if (dados.has("publishers")) {
                livro.setEditora(dados.getJSONArray("publishers").getJSONObject(0).getString("name"));
            } else {
                livro.setEditora("Editora n/d");
            }

            if (dados.has("publish_date")) {
                String dataCrua = dados.getString("publish_date");
                Matcher m = Pattern.compile("\\d{4}").matcher(dataCrua);
                livro.setDataPublicacao(m.find() ? m.group() : dataCrua);
            } else {
                livro.setDataPublicacao("S/D");
            }

            byte[] capaBytes = baixarCapa(isbn);
            if (capaBytes != null) {
                livro.setCapaImagem(capaBytes);
            }

            log.info("Dados do livro '{}' processados com sucesso.", livro.getTitulo());
            return livro;

        } catch (Exception e) {
            log.error("Erro ao processar dados da OpenLibrary", e);
            throw new ServiceException("Erro de comunicação com a OpenLibrary: " + e.getMessage());
        }
    }

    /**
     * Realiza o download da imagem da capa do livro (Tamanho Médio).
     *
     * @param isbn O ISBN do livro.
     * @return Array de bytes da imagem (JPG) ou {@code null} se a imagem não existir ou for inválida.
     */
    public byte[] baixarCapa(String isbn) {
        log.debug("Iniciando download da capa para ISBN: {}", isbn);
        try {
            String urlCapa = "https://covers.openlibrary.org/b/isbn/" + isbn + "-M.jpg";

            try (InputStream in = abrirStreamUrl(urlCapa); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                if (in == null) return null;

                byte[] buffer = new byte[1024];
                int n;
                while ((n = in.read(buffer)) != -1) {
                    out.write(buffer, 0, n);
                }

                byte[] imagemBytes = out.toByteArray();

                if (imagemBytes.length < 100) {
                    log.debug("Imagem baixada é muito pequena ({} bytes), provável pixel transparente. Ignorando.", imagemBytes.length);
                    return null;
                }

                return imagemBytes;
            }
        } catch (Exception e) {
            log.warn("Não foi possível baixar a capa para ISBN {}: {}", isbn, e.getMessage());
            return null;
        }
    }

    /**
     * Método auxiliar protegido para executar a requisição HTTP GET bruta.
     *
     * @param urlString A URL completa para a requisição.
     * @return O corpo da resposta em formato String (JSON).
     * @throws Exception Caso o código de resposta HTTP seja diferente de 200 (OK).
     */
    protected String fazerRequisicaoHttp(String urlString) throws Exception {
        log.debug("Executando GET: {}", urlString);

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        if (conn.getResponseCode() != 200) {
            log.error("Erro HTTP {}: {}", conn.getResponseCode(), urlString);
            throw new Exception("HTTP Erro: " + conn.getResponseCode());
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) response.append(line);
            return response.toString();
        }
    }

    /**
     * Método auxiliar protegido para abrir o stream de uma URL.
     * Necessário para permitir o mock do download da imagem nos testes.
     */
    protected InputStream abrirStreamUrl(String urlString) throws Exception {
        return new URL(urlString).openStream();
    }
}