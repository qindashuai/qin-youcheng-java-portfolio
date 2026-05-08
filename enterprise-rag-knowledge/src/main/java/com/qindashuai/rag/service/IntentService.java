package com.qindashuai.rag.service;

public interface IntentService {

    String recognizeIntent(String question);

    String buildSystemPrompt(String intentType);
}
