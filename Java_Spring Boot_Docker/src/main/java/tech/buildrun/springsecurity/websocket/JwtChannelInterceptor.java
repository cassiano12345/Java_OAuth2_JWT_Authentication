package tech.buildrun.springsecurity.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.repository.UserRepository;

import java.security.Principal;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {
    @Value("${jwt.public.key}")
    private RSAPublicKey publicKey;
    private final JwtDecoder jwtDecoder;

    private final UserRepository userRepository;

    public JwtChannelInterceptor(
            JwtDecoder jwtDecoder,
            UserRepository userRepository
    ) {

        this.jwtDecoder = jwtDecoder;
        this.userRepository = userRepository;

    }

    @Override
    public Message<?> preSend(
            @NonNull Message<?> message,
            @NonNull MessageChannel channel
    ) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            List<String> authorizationHeaders =
                    accessor.getNativeHeader("Authorization");

            if (authorizationHeaders == null
                    || authorizationHeaders.isEmpty()) {

                throw new RuntimeException(
                        "Authorization header não enviado."
                );

            }

            String bearerToken = authorizationHeaders.get(0);

            if (!bearerToken.startsWith("Bearer ")) {

                throw new RuntimeException(
                        "Bearer token inválido."
                );

            }

            String token = bearerToken.substring(7).trim();

            Jwt jwt = NimbusJwtDecoder
                    .withPublicKey(publicKey)
                    .build()
                    .decode(token);

            String username = jwt.getSubject();

            User user = userRepository
                    .findByUsername(username)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Utilizador não encontrado."
                            ));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            accessor.setUser(authentication);

        }

        return message;
    }

}