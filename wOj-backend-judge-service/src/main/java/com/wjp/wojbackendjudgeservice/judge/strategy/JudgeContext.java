package com.wjp.wojbackendjudgeservice.judge.strategy;

import com.wjp.wojbackendcommon.model.codesandbox.JudgeInfo;
import com.wjp.wojbackendcommon.model.dto.question.JudgeCase;
import com.wjp.wojbackendcommon.model.entity.Question;
import com.wjp.wojbackendcommon.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文(用于定义在策略模式中传递的参数)
 */
@Data
public class JudgeContext {
    private List<String> inputList;

    private List<String> outputList;

    private JudgeInfo judgeInfo;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;
}
