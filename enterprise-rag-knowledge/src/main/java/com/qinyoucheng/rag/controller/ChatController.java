package com.qinyoucheng.rag.controller;

import com.qinyoucheng.rag.common.PageResult;
import com.qinyoucheng.rag.common.Result;
import com.qinyoucheng.rag.dto.ChatRequestDTO;
import com.qinyoucheng.rag.dto.ChatResponseDTO;
import com.qinyoucheng.rag.service.ChatService;
import com.qinyoucheng.rag.vo.ChatMessageVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/ask")
    public Result<ChatResponseDTO> ask(@Valid @RequestBody ChatRequestDTO request) {
        ChatResponseDTO response = chatService.chat(request);
        return Result.success(response);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public Result<PageResult<ChatMessageVO>> getMessages(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResult<ChatMessageVO> result = chatService.getConversationMessages(conversationId, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/conversations")
    public Result<PageResult<ChatMessageVO>> listConversations(
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<ChatMessageVO> result = chatService.listConversations(userId, pageNum, pageSize);
        return Result.success(result);
    }

    @DeleteMapping("/conversations/{conversationId}")
    public Result<Void> deleteConversation(@PathVariable String conversationId) {
        chatService.deleteConversation(conversationId);
        return Result.success();
    }
}
