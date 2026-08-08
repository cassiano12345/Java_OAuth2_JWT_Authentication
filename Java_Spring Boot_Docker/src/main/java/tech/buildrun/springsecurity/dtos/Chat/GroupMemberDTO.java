package tech.buildrun.springsecurity.dtos.Chat;

import java.util.UUID;

public record GroupMemberDTO(
        UUID id,
        String name,
        String letter,
        String role
) {}
