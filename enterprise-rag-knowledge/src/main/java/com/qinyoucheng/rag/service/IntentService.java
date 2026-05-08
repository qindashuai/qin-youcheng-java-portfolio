package com.qinyoucheng.rag.service;

public interface IntentService {

    String recognizeIntent(String question);

    String buildSystemPrompt(String intentType);
}
