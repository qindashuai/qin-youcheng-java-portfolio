package com.qinyoucheng.toolkit.excel;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExcelAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ExcelExportUtil excelExportUtil() {
        return new ExcelExportUtil();
    }
}
