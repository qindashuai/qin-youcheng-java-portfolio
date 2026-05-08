package com.qinyoucheng.rag.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qinyoucheng.rag.common.BusinessException;
import com.qinyoucheng.rag.common.PageResult;
import com.qinyoucheng.rag.common.ResultCode;
import com.qinyoucheng.rag.config.OllamaConfig;
import com.qinyoucheng.rag.config.VectorStoreConfig;
import com.qinyoucheng.rag.dto.ChatRequestDTO;
import com.qinyoucheng.rag.dto.ChatResponseDTO;
import com.qinyoucheng.rag.entity.ChatConversation;
import com.qinyoucheng.rag.entity.ChatMessage;
import com.qinyoucheng.rag.entity.DocumentChunk;
import com.qinyoucheng.rag.mapper.ChatConversationMapper;
import com.qinyoucheng.rag.mapper.ChatMessageMapper;
import com.qinyoucheng.rag.service.ChatService;
import com.qinyoucheng.rag.service.IntentService;
import com.qinyoucheng.rag.service.VectorService;
import com.qinyoucheng.rag.util.RedisUtil;
import com.qinyoucheng.rag.vo.ChatMessageVO;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private final ChatConversationMapper conversationMapper;
    private final ChatMessageMapper messageMapper;
    private final VectorService vectorService;
    private final IntentService intentService;
    private final ChatLanguageModel chatModel;
    private final OllamaConfig ollamaConfig;
    private final VectorStoreConfig vectorStoreConfig;
    private final RedisUtil redisUtil;

    private static final String CACHE_PREFIX_HOT = "rag:hot:";
    private static final String CACHE_PREFIX_CONTEXT = "rag:context:";
    private static final int MAX_HISTORY_TURNS = 5;

    public ChatServiceImpl(ChatConversationMapper conversationMapper,
                           ChatMessageMapper messageMapper,
                           VectorService vectorService,
                           IntentService intentService,
                           ChatLanguageModel chatModel,
                           OllamaConfig ollamaConfig,
                           VectorStoreConfig vectorStoreConfig,
                           RedisUtil redisUtil) {
        this.conversationMapper = conversationMapper;
        this.messageMapper = messageMapper;
        this.vectorService = vectorService;
        this.intentService = intentService;
        this.chatModel = chatModel;
        this.ollamaConfig = ollamaConfig;
        this.vectorStoreConfig = vectorStoreConfig;
        this.redisUtil = redisUtil;
    }

    @Override
    public ChatResponseDTO chat(ChatRequestDTO request) {
        long startTime = System.currentTimeMillis();

        String conversationId = request.getConversationId();
        if (conversationId == null || conversationId.isEmpty()) {
            conversationId = UUID.randomUUID().toString().replace("-", "");
            createConversation(conversationId, request);
        }

        String intentType = intentService.recognizeIntent(request.getQuestion());

        Object cachedAnswer = redisUtil.get(CACHE_PREFIX_HOT + request.getQuestion().hashCode());
        if (cachedAnswer != null) {
            log.info("命中热门问题缓存: question={}", request.getQuestion());
            ChatResponseDTO response = new ChatResponseDTO();
            response.setConversationId(conversationId);
            response.setAnswer(cachedAnswer.toString());
            response.setIntentType(intentType);
            response.setResponseTime((int) (System.currentTimeMillis() - startTime));
            return response;
        }

        List<DocumentChunk> relevantChunks = new ArrayList<>();
        if (request.getKnowledgeBaseId() != null) {
            relevantChunks = vectorService.search(
                    request.getKnowledgeBaseId(),
                    request.getQuestion(),
                    vectorStoreConfig.getTopK(),
                    vectorStoreConfig.getSimilarityThreshold());
        }

        String context = relevantChunks.stream()
                .map(DocumentChunk::getContent)
                .collect(Collectors.joining("\n\n"));

        String systemPrompt = intentService.buildSystemPrompt(intentType);

        List<dev.langchain4j.data.message.ChatMessage> messages = buildMessages(conversationId, systemPrompt, context, request.getQuestion());

        String answer;
        try {
            dev.langchain4j.model.output.Response<AiMessage> response =
                    chatModel.generate(messages);
            answer = response.content().text();
        } catch (Exception e) {
            log.error("LLM调用失败", e);
            throw new BusinessException(ResultCode.LLM_CALL_ERROR, "LLM调用失败: " + e.getMessage());
        }

        int responseTime = (int) (System.currentTimeMillis() - startTime);

        saveUserMessage(conversationId, request.getQuestion(), intentType);
        Long assistantMsgId = saveAssistantMessage(conversationId, answer, intentType,
                relevantChunks, responseTime);

        updateConversation(conversationId, request.getQuestion());

        redisUtil.set(CACHE_PREFIX_HOT + request.getQuestion().hashCode(), answer, 1, TimeUnit.HOURS);
        cacheConversationContext(conversationId, request.getQuestion(), answer);

        ChatResponseDTO responseDTO = new ChatResponseDTO();
        responseDTO.setConversationId(conversationId);
        responseDTO.setAnswer(answer);
        responseDTO.setIntentType(intentType);
        responseDTO.setResponseTime(responseTime);

        List<ChatResponseDTO.SourceReference> sources = relevantChunks.stream()
                .limit(3)
                .map(chunk -> {
                    ChatResponseDTO.SourceReference ref = new ChatResponseDTO.SourceReference();
                    ref.setChunkId(chunk.getId());
                    ref.setContent(chunk.getContent().length() > 200
                            ? chunk.getContent().substring(0, 200) + "..."
                            : chunk.getContent());
                    return ref;
                })
                .collect(Collectors.toList());
        responseDTO.setSources(sources);

        return responseDTO;
    }

    @Override
    public PageResult<ChatMessageVO> getConversationMessages(String conversationId, int pageNum, int pageSize) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getConversationId, conversationId)
                .orderByAsc(ChatMessage::getCreateTime);

        Page<ChatMessage> page = messageMapper.selectPage(
                new Page<>(pageNum, pageSize), wrapper);

        List<ChatMessageVO> vos = page.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.of(page.getTotal(), pageNum, pageSize, vos);
    }

    @Override
    public PageResult<ChatMessageVO> listConversations(String userId, int pageNum, int pageSize) {
        LambdaQueryWrapper<ChatConversation> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(ChatConversation::getUserId, userId);
        }
        wrapper.orderByDesc(ChatConversation::getUpdateTime);

        Page<ChatConversation> page = conversationMapper.selectPage(
                new Page<>(pageNum, pageSize), wrapper);

        List<ChatMessageVO> vos = page.getRecords().stream()
                .map(conv -> {
                    ChatMessageVO vo = new ChatMessageVO();
                    vo.setConversationId(conv.getConversationId());
                    vo.setContent(conv.getTitle());
                    vo.setCreateTime(conv.getCreateTime());
                    return vo;
                })
                .collect(Collectors.toList());

        return PageResult.of(page.getTotal(), pageNum, pageSize, vos);
    }

    @Override
    public void deleteConversation(String conversationId) {
        LambdaQueryWrapper<ChatMessage> msgWrapper = new LambdaQueryWrapper<>();
        msgWrapper.eq(ChatMessage::getConversationId, conversationId);
        messageMapper.delete(msgWrapper);

        LambdaQueryWrapper<ChatConversation> convWrapper = new LambdaQueryWrapper<>();
        convWrapper.eq(ChatConversation::getConversationId, conversationId);
        conversationMapper.delete(convWrapper);

        redisUtil.delete(CACHE_PREFIX_CONTEXT + conversationId);
    }

    private void createConversation(String conversationId, ChatRequestDTO request) {
        ChatConversation conversation = new ChatConversation();
        conversation.setConversationId(conversationId);
        conversation.setTitle(request.getQuestion().length() > 50
                ? request.getQuestion().substring(0, 50) + "..."
                : request.getQuestion());
        conversation.setUserId(request.getUserId());
        conversation.setKnowledgeBaseId(request.getKnowledgeBaseId());
        conversation.setMessageCount(0);
        conversation.setStatus(1);
        conversation.setCreateTime(LocalDateTime.now());
        conversation.setUpdateTime(LocalDateTime.now());
        conversationMapper.insert(conversation);
    }

    private void saveUserMessage(String conversationId, String question, String intentType) {
        ChatMessage message = new ChatMessage();
        message.setConversationId(conversationId);
        message.setRole("USER");
        message.setContent(question);
        message.setIntentType(intentType);
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());
        messageMapper.insert(message);
    }

    private Long saveAssistantMessage(String conversationId, String answer, String intentType,
                                       List<DocumentChunk> sources, int responseTime) {
        ChatMessage message = new ChatMessage();
        message.setConversationId(conversationId);
        message.setRole("ASSISTANT");
        message.setContent(answer);
        message.setIntentType(intentType);
        message.setSourceChunks(sources.stream()
                .map(chunk -> String.valueOf(chunk.getId()))
                .collect(Collectors.joining(",")));
        message.setModelName(ollamaConfig.getChatModel());
        message.setResponseTime(responseTime);
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());
        messageMapper.insert(message);
        return message.getId();
    }

    private void updateConversation(String conversationId, String question) {
        LambdaQueryWrapper<ChatConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatConversation::getConversationId, conversationId);
        ChatConversation conversation = conversationMapper.selectOne(wrapper);
        if (conversation != null) {
            conversation.setMessageCount(conversation.getMessageCount() + 1);
            conversation.setUpdateTime(LocalDateTime.now());
            conversationMapper.updateById(conversation);
        }
    }

    private List<dev.langchain4j.data.message.ChatMessage> buildMessages(String conversationId, String systemPrompt,
                                                 String context, String question) {
        List<dev.langchain4j.data.message.ChatMessage> messages = new ArrayList<>();

        messages.add(SystemMessage.from(systemPrompt));

        if (context != null && !context.isEmpty()) {
            messages.add(SystemMessage.from("以下是从知识库中检索到的相关内容，请基于这些内容回答用户问题：\n\n" + context));
        }

        List<ChatMessage> history = getRecentHistory(conversationId, MAX_HISTORY_TURNS);
        for (ChatMessage msg : history) {
            if ("USER".equals(msg.getRole())) {
                messages.add(UserMessage.from(msg.getContent()));
            } else if ("ASSISTANT".equals(msg.getRole())) {
                messages.add(AiMessage.from(msg.getContent()));
            }
        }

        messages.add(UserMessage.from(question));
        return messages;
    }

    private List<ChatMessage> getRecentHistory(String conversationId, int maxTurns) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getConversationId, conversationId)
                .orderByDesc(ChatMessage::getCreateTime);
        Page<ChatMessage> page = messageMapper.selectPage(
                new Page<>(1, maxTurns * 2), wrapper);
        return page.getRecords();
    }

    private void cacheConversationContext(String conversationId, String question, String answer) {
        redisUtil.set(CACHE_PREFIX_CONTEXT + conversationId,
                question + "|||" + answer, 30, TimeUnit.MINUTES);
    }

    private ChatMessageVO convertToVO(ChatMessage message) {
        ChatMessageVO vo = new ChatMessageVO();
        BeanUtils.copyProperties(message, vo);
        return vo;
    }
}
