package tech.buildrun.springsecurity.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.repository.UserRepository;

import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;

    public JwtHandshakeInterceptor(JwtDecoder jwtDecoder,
                                   UserRepository userRepository) {

        this.jwtDecoder = jwtDecoder;
        this.userRepository = userRepository;
    }

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull org.springframework.http.server.ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) {

        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }

        String token = servletRequest
                .getServletRequest()
                .getParameter("token");

        if (token == null || token.isBlank()) {
            return false;
        }

        try {

            Jwt jwt = jwtDecoder.decode(token);

            String username = jwt.getSubject();

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() ->
                            new RuntimeException("Utilizador não encontrado."));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            attributes.put("authentication", authentication);

            return true;

        } catch (Exception ex) {

            return false;

        }
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request,
                               @NonNull org.springframework.http.server.ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler,
                               @Nullable Exception exception) {

        // Não é necessário fazer nada aqui.

    }
}