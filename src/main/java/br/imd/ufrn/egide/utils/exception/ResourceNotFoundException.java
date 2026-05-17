package br.imd.ufrn.egide.utils.exception;

// Exceção lançada quando um recurso solicitado não é encontrado no banco de dados.
// Mapeada para HTTP 404 pelo HandlerGlobalException.
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
