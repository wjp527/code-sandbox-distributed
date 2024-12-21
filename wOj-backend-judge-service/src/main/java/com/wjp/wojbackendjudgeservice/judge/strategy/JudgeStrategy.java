package com.wjp.wojbackendjudgeservice.judge.strategy;


import com.wjp.wojbackendcommon.model.codesandbox.JudgeInfo;

/**
 * 策略模式【判断策略】
 */
public interface JudgeStrategy {
    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
