package com.xxl.job.core.handler.annotation;

import java.lang.annotation.*;

/**
 *
 * 二次疯转，版本和xuxueli不一致
 * @Author HanKeQi
 * @Date 2020/4/18 2:01 下午
 * @Version 1.0
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XxlJob {

    /**
     * jobhandler name
     */
    String value() default "";

    /**
     * init handler, invoked when JobThread init
     */
    String init() default "";

    /**
     * destroy handler, invoked when JobThread destroy
     */
    String destroy() default "";


}
