package com.wjp.wojbackendcommon.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 判题信息消息类型枚举
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://wjp.icu">编程导航知识星球</a>
 */
public enum JudgeInfoMessageEnum {

    ACCEPTED("成功", "Accepted"),

    WRONG_ANSWER("答案错误", "Wrong Answer"),
    COMPILE_ERROR("编译错误", "Compile Error"),

    MEMORY_LIMIT_EXCEEDED("内存溢出", "Memory Limit Exceeded"),


    TIME_LIMIT_EXCEEDED("时间超限", "Time Limit Exceeded"),


    RUNTIME_ERROR("运行错误", "Runtime Error"),


    SYSTEM_ERROR("系统错误", "System Error"),


    PRESENTATION_ERROR("未知错误", "Presentation Error"),
    OUTPUT_LIMIT_EXCEEDED("输出溢出", "Output limit Exceeded"),
    DANGEROUS_OPERATION("未知错误", "Dangerous Operation"),
    Waiting("运行中", "Waiting");


    private final String text;

    private final String value;

    JudgeInfoMessageEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static JudgeInfoMessageEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (JudgeInfoMessageEnum anEnum : JudgeInfoMessageEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
