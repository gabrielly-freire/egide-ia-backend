package br.imd.ufrn.egide.utils.exception;


import org.springframework.http.HttpStatus;

// Exceção de regra de negócio com código HTTP explícito.
// Usada para sinalizar violações de regras do domínio (conflito, validação, acesso proibido, etc.)
// sem expor detalhes de infraestrutura ao cliente. O HandlerGlobalException captura e serializa.
public class BusinessException extends RuntimeException {

    private final HttpStatus httpStatusCode;

    public BusinessException(String message, HttpStatus statusCode) {
        super(message);
        this.httpStatusCode = statusCode;
    }

    public HttpStatus getHttpStatusCode() {
        return httpStatusCode;
    }

}

