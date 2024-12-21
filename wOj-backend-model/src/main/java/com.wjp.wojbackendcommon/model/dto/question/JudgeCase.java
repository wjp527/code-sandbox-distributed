package com.wjp.wojbackendcommon.model.dto.question;

import lombok.Data;

/**
 * 题目用例(json 数组)
 * @author wjp
 */

@Data
public class JudgeCase {
    /**
     * 输入
     */
    private String input;

    /**
     * 输出
     */
    private String output;

}
