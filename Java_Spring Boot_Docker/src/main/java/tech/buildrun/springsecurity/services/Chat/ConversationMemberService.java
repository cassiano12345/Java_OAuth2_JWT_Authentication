package tech.buildrun.springsecurity.services.Chat;

import org.springframework.stereotype.Service;
import tech.buildrun.springsecurity.entities.Chat.ConversationMember;
import tech.buildrun.springsecurity.entities.Chat.ConversationMemberRole;
import tech.buildrun.springsecurity.entities.User;
import tech.buildrun.springsecurity.repository.Chat.ConversationMemberRepository;
import tech.buildrun.springsecurity.services.AuthenticatedUserService;

import java.util.List;
import java.util.UUID;

@Service
public class ConversationMemberService {

    private final ConversationMemberRepository conversationMemberRepository;
    private final AuthenticatedUserService authenticatedUserService;

    public ConversationMemberService(
            ConversationMemberRepository conversationMemberRepository, AuthenticatedUserService authenticatedUserService
    ) {
        this.conversationMemberRepository = conversationMemberRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    // Buscar um membro específico de uma conversa
    public ConversationMember findMember(UUID conversationId) {
        User user = authenticatedUserService.getAuthenticatedUser();
        return conversationMemberRepository.findByConversation_ConversationIdAndUser_UserId(conversationId,user.getUserId())
                .orElseThrow(() ->new RuntimeException("Usuário não pertence a esta conversa."));
    }

    // Buscar todos os membros de uma conversa
    public List<ConversationMember> findMembersByConversation(
            UUID conversationId
    ) {
        return conversationMemberRepository
                .findByConversation_ConversationId(conversationId);
    }

    // Buscar todas as conversas de um usuário
    public List<ConversationMember> findConversationsByUser() {
        User user = authenticatedUserService.getAuthenticatedUser();
        return conversationMemberRepository
                .findByUser_UserId(user.getUserId());
    }

    // Verificar se um usuário pertence a uma conversa
    public boolean isMember(UUID conversationId) {
        User user = authenticatedUserService.getAuthenticatedUser();
        return conversationMemberRepository
                .existsByConversation_ConversationIdAndUser_UserId(
                        conversationId,
                        user.getUserId()
                );
    }

    // Verificar se um usuário é administrador
    public boolean isAdmin(UUID conversationId) {
        User user = authenticatedUserService.getAuthenticatedUser();
        return conversationMemberRepository
                .findByConversation_ConversationIdAndUser_UserId(
                        conversationId,
                        user.getUserId()
                )
                .map(member ->
                        member.getRole() == ConversationMemberRole.ADMIN
                )
                .orElse(false);
    }

    // Alterar a função do membro
    public ConversationMember updateRole(UUID conversationId,UUID userId,ConversationMemberRole role) {
        ConversationMember member = findMember(
                conversationId
        );

        member.setRole(role);

        return conversationMemberRepository.save(member);
    }

    // Salvar um membro
    public ConversationMember save(
            ConversationMember conversationMember
    ) {
        return conversationMemberRepository.save(conversationMember);
    }

    // Remover um membro
    public void delete(UUID conversationId) {
        ConversationMember member = findMember(
                conversationId
        );

        conversationMemberRepository.delete(member);
    }

}