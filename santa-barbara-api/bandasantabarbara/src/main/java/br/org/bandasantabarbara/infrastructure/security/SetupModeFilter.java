package br.org.bandasantabarbara.infrastructure.security;

import br.org.bandasantabarbara.application.DefaultMessageResponse;
import br.org.bandasantabarbara.repositories.MembroRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SetupModeFilter extends OncePerRequestFilter {

    private final MembroRepository membroRepository;
    private final ObjectMapper objectMapper;
    private volatile boolean adminJaCriado = false;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        String method = request.getMethod();

        if (!adminJaCriado) {
            adminJaCriado = membroRepository.existeAdminCadastrado();
        }

        if (adminJaCriado && "/setup".equals(path) && "POST".equalsIgnoreCase(method)) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        if (!adminJaCriado && !("/setup".equals(path) && "POST".equalsIgnoreCase(method))) {

            var errorResponse = new DefaultMessageResponse(
                    "Sistema em modo de inicialização. Crie o administrador em POST /api/v1/setup."
            );

            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            objectMapper.writeValue(response.getWriter(), errorResponse);
            return;
        }

        filterChain.doFilter(request, response);
    }
}