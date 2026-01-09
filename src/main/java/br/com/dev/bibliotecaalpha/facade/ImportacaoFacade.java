package br.com.dev.bibliotecaalpha.facade;

import br.com.dev.bibliotecaalpha.exception.ServiceException;
import br.com.dev.bibliotecaalpha.service.ImportacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Fachada (Facade) especializada em operações de entrada de dados em massa.
 * <p>
 * Atua como um simplificador para a camada de visualização, isolando a complexidade
 * do serviço de importação e centralizando o tratamento de chamadas para
 * processamento de arquivos CSV.
 * </p>
 *
 * @author Luccas Cabrini
 * @version 1.0
 */
@Component
public class ImportacaoFacade {

    private static final Logger log = LoggerFactory.getLogger(ImportacaoFacade.class);

    @Autowired
    private ImportacaoService importacaoService;

    /**
     * Coordena a importação de livros a partir de um arquivo externo.
     *
     * @param caminhoArquivo O caminho do arquivo CSV no sistema de arquivos.
     * @throws ServiceException Caso ocorra erro de leitura, formato inválido, arquivo inexistente
     * ou violação de regras de negócio durante a persistência.
     */
    public void importarLivros(String caminhoArquivo) throws ServiceException {
        log.info("Recebida solicitação de importação do arquivo: {}", caminhoArquivo);
        importacaoService.importarArquivoCSV(caminhoArquivo);
    }
}