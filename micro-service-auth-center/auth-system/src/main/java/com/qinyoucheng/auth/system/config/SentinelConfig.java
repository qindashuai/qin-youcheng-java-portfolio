package com.qinyoucheng.auth.system.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class SentinelConfig {

    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

    @PostConstruct
    public void initRules() {
        List<FlowRule> rules = new ArrayList<>();

        FlowRule loginRule = new FlowRule();
        loginRule.setResource("auth-login");
        loginRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        loginRule.setCount(10);
        rules.add(loginRule);

        FlowRule apiRule = new FlowRule();
        apiRule.setResource("auth-api");
        apiRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        apiRule.setCount(50);
        rules.add(apiRule);

        FlowRuleManager.loadRules(rules);
    }
}
