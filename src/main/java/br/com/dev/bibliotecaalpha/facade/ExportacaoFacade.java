package br.com.dev.bibliotecaalpha.facade;

import br.com.dev.bibliotecaalpha.exception.ServiceException;
import br.com.dev.bibliotecaalpha.service.ExportacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Fachada (Facade) especializada em operações de saída de dados.
 * <p>
 * Responsável por expor os recursos de geração de relatórios e exportação
 * de arquivos para a camada de apresentação, ocultando a lógica de formatação
 * e escrita em disco.
 * </p>
 *
 * @author Luccas Cabrini
 * @version 1.0
 */
@Component
public class ExportacaoFacade {

    private static final Logger log = LoggerFactory.getLogger(ExportacaoFacade.class);

    @Autowired
    private ExportacaoService exportacaoService;

    /**
     * Coordena a exportação de todo o acervo cadastrado para um arquivo CSV.
     *
     * @param caminhoArquivo O caminho completo onde o arquivo de relatório será salvo.
     * @throws ServiceException Caso ocorra erro de permissão ou falha de escrita no disco.
     */
    public void exportarLivros(String caminhoArquivo) throws ServiceException {
        log.info("Iniciando solicitação de exportação para o arquivo: {}", caminhoArquivo);
        exportacaoService.exportarArquivoCSV(caminhoArquivo);
    }
}