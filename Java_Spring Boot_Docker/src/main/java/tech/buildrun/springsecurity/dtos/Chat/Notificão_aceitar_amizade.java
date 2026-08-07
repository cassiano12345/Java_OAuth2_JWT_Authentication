package tech.buildrun.springsecurity.dtos.Chat;

import java.util.List;

public record Notificão_aceitar_amizade(String mensagem, List<NotificationResponseDTO> notificacoes) {
}
