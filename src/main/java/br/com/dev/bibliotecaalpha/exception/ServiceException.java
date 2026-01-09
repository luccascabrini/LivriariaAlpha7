package br.com.dev.bibliotecaalpha.exception;

/**
 * Exceção personalizada para representar erros de regra de negócio ou falhas na camada de serviço.
 * @author Luccas Cabrini
 * @version 1.0
 */
public class ServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Cria uma nova exceção com uma mensagem de erro específica.
     *
     * @param message A mensagem descrevendo o erro (ex: "ISBN duplicado").
     */
    public ServiceException(String message) {
        super(message);
    }

}