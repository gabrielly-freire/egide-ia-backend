package br.imd.ufrn.egide.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
// Handler de acesso negado (HTTP 403) para usuários autenticados sem permissão no recurso.
// Retorna JSON padronizado no mesmo formato do ErrorMessage para consistência das respostas de erro.
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        String description = request.getMethod() + " " + request.getRequestURI();
        String body = """
                {"statusCode":403,"timestamp":"%s","message":"Acesso negado","description":"%s"}
                """.formatted(LocalDateTime.now(), description);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(body);
    }
}
