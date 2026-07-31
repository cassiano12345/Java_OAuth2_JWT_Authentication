package tech.buildrun.springsecurity.services.Chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tech.buildrun.springsecurity.entities.Chat.Attachment;
import tech.buildrun.springsecurity.entities.Chat.Message;
import tech.buildrun.springsecurity.repository.Chat.AttachmentRepository;
import tech.buildrun.springsecurity.repository.Chat.MessageRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final MessageRepository messageRepository;

    private final Path uploadDirectory =
            Paths.get("uploads/chat");

    public AttachmentService(
            AttachmentRepository attachmentRepository,
            MessageRepository messageRepository
    ) {
        this.attachmentRepository = attachmentRepository;
        this.messageRepository = messageRepository;
    }


    // Fazer upload de um anexo
    @Transactional
    public Attachment uploadAttachment(
            UUID messageId,
            MultipartFile file
    ) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException(
                    "O ficheiro não pode estar vazio."
            );
        }

        Message message = messageRepository
                .findById(messageId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Mensagem não encontrada."
                        )
                );

        try {

            // Criar a pasta caso não exista
            Files.createDirectories(uploadDirectory);

            // Nome original
            String originalFileName = file.getOriginalFilename();

            if (originalFileName == null ||
                    originalFileName.isBlank()) {

                throw new RuntimeException(
                        "Nome do ficheiro inválido."
                );
            }

            // Gerar um nome único para evitar conflitos
            String storedFileName =
                    UUID.randomUUID() + "_" + originalFileName;

            Path filePath =
                    uploadDirectory.resolve(storedFileName);

            // Guardar o ficheiro
            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // Criar Attachment
            Attachment attachment = new Attachment();

            attachment.setMessage(message);
            attachment.setFileName(originalFileName);
            attachment.setFileUrl(
                    "/uploads/chat/" + storedFileName
            );
            attachment.setFileType(
                    file.getContentType()
            );
            attachment.setFileSize(
                    file.getSize()
            );
            attachment.setUploadedAt(
                    LocalDateTime.now()
            );

            return attachmentRepository.save(attachment);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao guardar o ficheiro.",
                    e
            );
        }
    }


    // Buscar um anexo pelo ID
    public Attachment findById(
            UUID attachmentId
    ) {

        return attachmentRepository
                .findById(attachmentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Anexo não encontrado."
                        )
                );
    }


    // Buscar todos os anexos de uma mensagem
    public List<Attachment> findByMessage(
            UUID messageId
    ) {

        return attachmentRepository
                .findByMessage_MessageId(
                        messageId
                );
    }


    // Buscar anexos de uma mensagem ordenados
    public List<Attachment> findByMessageOrdered(
            UUID messageId
    ) {

        return attachmentRepository
                .findByMessage_MessageIdOrderByFileNameAsc(
                        messageId
                );
    }


    // Buscar todos os anexos de uma conversa
    public List<Attachment> findByConversation(
            UUID conversationId
    ) {

        return attachmentRepository
                .findByMessage_Conversation_ConversationId(
                        conversationId
                );
    }


    // Contar anexos de uma mensagem
    public long countByMessage(
            UUID messageId
    ) {

        return attachmentRepository
                .countByMessage_MessageId(
                        messageId
                );
    }


    // Verificar se uma mensagem possui anexos
    public boolean messageHasAttachments(
            UUID messageId
    ) {

        return attachmentRepository
                .existsByMessage_MessageId(
                        messageId
                );
    }


    // Apagar um anexo
    @Transactional
    public void deleteAttachment(
            UUID attachmentId
    ) {

        Attachment attachment = attachmentRepository
                .findById(attachmentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Anexo não encontrado."
                        )
                );

        try {

            String fileUrl = attachment.getFileUrl();

            if (fileUrl != null) {

                String fileName =
                        Paths.get(fileUrl)
                                .getFileName()
                                .toString();

                Path filePath =
                        uploadDirectory.resolve(fileName);

                Files.deleteIfExists(filePath);
            }

            attachmentRepository.delete(attachment);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao apagar o ficheiro.",
                    e
            );
        }
    }
}