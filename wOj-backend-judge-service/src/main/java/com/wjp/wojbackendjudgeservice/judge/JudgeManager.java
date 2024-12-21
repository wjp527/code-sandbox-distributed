package com.wjp.wojbackendjudgeservice.judge;

import com.wjp.wojbackendcommon.model.codesandbox.JudgeInfo;
import com.wjp.wojbackendcommon.model.entity.QuestionSubmit;
import com.wjp.wojbackendcommon.model.enums.QuestionSubmitLanguageEnum;
import com.wjp.wojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.wjp.wojbackendjudgeservice.judge.strategy.JavaLanguageJudgeStrategy;
import com.wjp.wojbackendjudgeservice.judge.strategy.JudgeContext;
import com.wjp.wojbackendjudgeservice.judge.strategy.JudgeStrategy;
import org.springframework.stereotype.Service;

/**
 * 判题管理(简化调用)
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        // 获取题目提交信息
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();

        // 默认策略
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();

        // 根据语言选择策略
        if(QuestionSubmitLanguageEnum.JAVA.getValue().equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }

        return  judgeStrategy.doJudge(judgeContext);
    }
}
