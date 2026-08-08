package tech.buildrun.springsecurity.dtos.Chat;

import java.util.List;

public record CreateGroupConversationDTO(
        String name,
        List<GroupMemberDTO> members
) {}
