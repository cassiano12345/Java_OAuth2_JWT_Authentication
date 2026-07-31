package tech.buildrun.springsecurity.controller.Chat;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import tech.buildrun.springsecurity.dtos.Chat.AttachmentResponseDTO;
import tech.buildrun.springsecurity.entities.Chat.Attachment;
import tech.buildrun.springsecurity.services.Chat.AttachmentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(
            AttachmentService attachmentService
    ) {
        this.attachmentService = attachmentService;
    }


    // Fazer upload de um anexo
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<AttachmentResponseDTO> uploadAttachment(
            @RequestParam("messageId") UUID messageId,
            @RequestParam("file") MultipartFile file
    ) {

        Attachment attachment =
                attachmentService.uploadAttachment(
                        messageId,
                        file
                );

        return ResponseEntity.ok(
                toResponseDTO(attachment)
        );
    }


    // Buscar um anexo pelo ID
    @PostMapping("/find")
    public ResponseEntity<AttachmentResponseDTO> findById(
            @RequestParam("attachmentId") UUID attachmentId
    ) {

        Attachment attachment =
                attachmentService.findById(
                        attachmentId
                );

        return ResponseEntity.ok(
                toResponseDTO(attachment)
        );
    }


    // Buscar anexos de uma mensagem
    @PostMapping("/find-by-message")
    public ResponseEntity<List<AttachmentResponseDTO>> findByMessage(
            @RequestParam("messageId") UUID messageId
    ) {

        List<Attachment> attachments =
                attachmentService.findByMessage(
                        messageId
                );

        List<AttachmentResponseDTO> response =
                attachments.stream()
                        .map(this::toResponseDTO)
                        .toList();

        return ResponseEntity.ok(response);
    }


    // Buscar anexos de uma mensagem ordenados
    @PostMapping("/find-by-message-ordered")
    public ResponseEntity<List<AttachmentResponseDTO>> findByMessageOrdered(
            @RequestParam("messageId") UUID messageId
    ) {

        List<Attachment> attachments =
                attachmentService.findByMessageOrdered(
                        messageId
                );

        List<AttachmentResponseDTO> response =
                attachments.stream()
                        .map(this::toResponseDTO)
                        .toList();

        return ResponseEntity.ok(response);
    }


    // Buscar anexos de uma conversa
    @PostMapping("/find-by-conversation")
    public ResponseEntity<List<AttachmentResponseDTO>> findByConversation(
            @RequestParam("conversationId") UUID conversationId
    ) {

        List<Attachment> attachments =
                attachmentService.findByConversation(
                        conversationId
                );

        List<AttachmentResponseDTO> response =
                attachments.stream()
                        .map(this::toResponseDTO)
                        .toList();

        return ResponseEntity.ok(response);
    }


    // Contar anexos de uma mensagem
    @PostMapping("/count-by-message")
    public ResponseEntity<Long> countByMessage(
            @RequestParam("messageId") UUID messageId
    ) {

        long count =
                attachmentService.countByMessage(
                        messageId
                );

        return ResponseEntity.ok(count);
    }


    // Verificar se uma mensagem possui anexos
    @PostMapping("/has-attachments")
    public ResponseEntity<Boolean> messageHasAttachments(
            @RequestParam("messageId") UUID messageId
    ) {

        boolean hasAttachments =
                attachmentService.messageHasAttachments(
                        messageId
                );

        return ResponseEntity.ok(hasAttachments);
    }


    // Apagar anexo
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAttachment(
            @RequestParam("attachmentId") UUID attachmentId
    ) {

        attachmentService.deleteAttachment(
                attachmentId
        );

        return ResponseEntity.noContent().build();
    }


    // Converter Entity para Response DTO
    private AttachmentResponseDTO toResponseDTO(
            Attachment attachment
    ) {

        return new AttachmentResponseDTO(
                attachment.getAttachmentId(),
                attachment.getMessage().getMessageId(),
                attachment.getFileName(),
                attachment.getFileUrl(),
                attachment.getFileType(),
                attachment.getFileSize(),
                attachment.getUploadedAt()
        );
    }
}