package com.qindashuai.rag.service;

import com.qindashuai.rag.common.PageResult;
import com.qindashuai.rag.dto.ChatRequestDTO;
import com.qindashuai.rag.dto.ChatResponseDTO;
import com.qindashuai.rag.vo.ChatMessageVO;

public interface ChatService {

    ChatResponseDTO chat(ChatRequestDTO request);

    PageResult<ChatMessageVO> getConversationMessages(String conversationId, int pageNum, int pageSize);

    PageResult<ChatMessageVO> listConversations(String userId, int pageNum, int pageSize);

    void deleteConversation(String conversationId);
}
