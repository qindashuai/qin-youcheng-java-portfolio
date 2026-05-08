package com.qindashuai.rag.service.impl;

import com.qindashuai.rag.service.IntentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class IntentServiceImpl implements IntentService {

    private static final Map<String, List<String>> INTENT_KEYWORDS = new HashMap<>();

    static {
        INTENT_KEYWORDS.put("POLICY", Arrays.asList(
                "制度", "规定", "规范", "管理办法", "条例", "政策", "规章", "守则",
                "考勤", "请假", "报销", "福利", "薪酬", "绩效", "入职", "离职",
                "加班", "休假", "年假", "社保", "公积金", "合同"
        ));
        INTENT_KEYWORDS.put("FAULT", Arrays.asList(
                "故障", "报错", "异常", "错误", "崩溃", "无法", "失败", "超时",
                "宕机", "慢", "卡顿", "死锁", "内存溢出", "CPU", "网络不通",
                "连接失败", "拒绝连接", "500", "404", "502", "503"
        ));
        INTENT_KEYWORDS.put("PROCESS", Arrays.asList(
                "流程", "步骤", "怎么操作", "如何办理", "操作指南", "审批",
                "申请", "提交", "审核", "流转", "办理", "指南", "教程",
                "怎么做", "怎样", "如何", "操作步骤", "使用方法"
        ));
    }

    private static final Map<String, String> INTENT_PROMPTS = new HashMap<>();

    static {
        INTENT_PROMPTS.put("POLICY",
                "你是企业内部制度查询助手。请根据提供的知识库内容，准确回答用户关于公司制度、规定、管理办法等方面的问题。" +
                "回答时请引用具体的制度条款，确保信息的准确性和权威性。如果知识库中没有相关内容，请明确告知。");

        INTENT_PROMPTS.put("FAULT",
                "你是企业故障排查助手。请根据提供的知识库内容，帮助用户诊断和解决系统故障问题。" +
                "回答时请提供清晰的排查步骤和解决方案，按照从易到难的顺序排列。如果知识库中没有相关内容，请提供通用的排查思路。");

        INTENT_PROMPTS.put("PROCESS",
                "你是企业业务流程助手。请根据提供的知识库内容，详细解答用户关于业务操作流程、审批流程等方面的问题。" +
                "回答时请按照步骤顺序清晰描述，标注关键节点和注意事项。如果知识库中没有相关内容，请明确告知。");

        INTENT_PROMPTS.put("OTHER",
                "你是企业智能问答助手。请根据提供的知识库内容，准确回答用户的问题。" +
                "如果知识库中没有相关内容，请基于你的知识给出合理的回答，但要明确标注信息来源。");
    }

    @Override
    public String recognizeIntent(String question) {
        if (question == null || question.trim().isEmpty()) {
            return "OTHER";
        }

        Map<String, Integer> scores = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : INTENT_KEYWORDS.entrySet()) {
            int score = 0;
            for (String keyword : entry.getValue()) {
                if (question.contains(keyword)) {
                    score++;
                }
            }
            if (score > 0) {
                scores.put(entry.getKey(), score);
            }
        }

        if (scores.isEmpty()) {
            return "OTHER";
        }

        return scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("OTHER");
    }

    @Override
    public String buildSystemPrompt(String intentType) {
        return INTENT_PROMPTS.getOrDefault(intentType, INTENT_PROMPTS.get("OTHER"));
    }
}
