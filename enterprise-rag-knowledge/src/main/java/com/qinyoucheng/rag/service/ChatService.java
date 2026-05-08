package com.qinyoucheng.rag.service;

import com.qinyoucheng.rag.common.PageResult;
import com.qinyoucheng.rag.dto.ChatRequestDTO;
import com.qinyoucheng.rag.dto.ChatResponseDTO;
import com.qinyoucheng.rag.vo.ChatMessageVO;

public interface ChatService {

    ChatResponseDTO chat(ChatRequestDTO request);

    PageResult<ChatMessageVO> getConversationMessages(String conversationId, int pageNum, int pageSize);

    PageResult<ChatMessageVO> listConversations(String userId, int pageNum, int pageSize);

    void deleteConversation(String conversationId);
}
