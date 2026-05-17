package br.imd.ufrn.egide.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
// Entry point de autenticação (HTTP 401) para requisições sem token ou com token inválido.
// Retorna JSON padronizado no mesmo formato do ErrorMessage para consistência das respostas de erro.
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        String description = request.getMethod() + " " + request.getRequestURI();
        String body = """
                {"statusCode":401,"timestamp":"%s","message":"Não autenticado","description":"%s"}
                """.formatted(LocalDateTime.now(), description);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(body);
    }
}
