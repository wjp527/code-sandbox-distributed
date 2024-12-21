package com.wjp.wojbackendcommon.model.dto.questionsubmit;

import com.wjp.wojbackendcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 提交题目查询请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://wjp.icu">编程导航知识星球</a>
 */
@Data
@EqualsAndHashCode(callSuper = true) // ✨ 重写方法
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 提交状态
     */
    private Integer status;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 用户 id
     */
    private Long userId;



    private static final long serialVersionUID = 1L;
}