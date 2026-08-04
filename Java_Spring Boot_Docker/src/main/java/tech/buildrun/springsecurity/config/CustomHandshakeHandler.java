package tech.buildrun.springsecurity.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) attributes.get("authentication");

        if (authentication != null) {
            return authentication;
        }

        return super.determineUser(request, wsHandler, attributes);
    }

}