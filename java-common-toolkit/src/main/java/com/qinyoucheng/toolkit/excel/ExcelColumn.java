package com.qinyoucheng.toolkit.excel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelColumn {

    String name() default "";

    int order() default 0;

    int width() default 20;

    String dateFormat() default "";

    String defaultValue() default "";

    boolean export() default true;
}
