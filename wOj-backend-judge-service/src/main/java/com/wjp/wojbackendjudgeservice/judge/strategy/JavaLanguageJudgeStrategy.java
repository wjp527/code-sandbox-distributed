package com.wjp.wojbackendjudgeservice.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.wjp.wojbackendcommon.model.codesandbox.JudgeInfo;
import com.wjp.wojbackendcommon.model.dto.question.JudgeCase;
import com.wjp.wojbackendcommon.model.dto.question.JudgeConfig;
import com.wjp.wojbackendcommon.model.entity.Question;
import com.wjp.wojbackendcommon.model.enums.JudgeInfoMessageEnum;

import java.util.List;

/**
 * Java程序 判题策略
 */
public class JavaLanguageJudgeStrategy implements JudgeStrategy {
    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Question question = judgeContext.getQuestion();


        // 用户的
        Long memory = judgeInfo.getMemory();
        Long time = judgeInfo.getTime();

        // 默认为正确【通过】的状态
        JudgeInfoMessageEnum judgeInfoMessage = JudgeInfoMessageEnum.ACCEPTED;

        // 设置返回消息内容
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        // 设置实际执行的内存
        judgeInfoResponse.setMemory(memory);
        // 设置执行的时间
        judgeInfoResponse.setTime(time);

        // 代码沙箱返回的响应数据
        // 返回的输出用例
        // 判断输入输出用例的长度是否一致
//        if (inputList.size() != outputList.size()) {
//            judgeInfoMessage = JudgeInfoMessageEnum.WRONG_ANSWER;
//            // 设置消息
//            judgeInfoResponse.setMessage(judgeInfoMessage.getValue());
//            return judgeInfoResponse;
//
//        }

        // 两者的输出数据是否一致
        for (int i = 0; i < judgeCaseList.size(); i++) {
            // 题目的输出用例【答案】
            String outputByAnswer = judgeCaseList.get(i).getOutput();
            // 用户的输出用例【用户】
            String outputByUser = outputList.get(i);
            // 去掉换行符
            outputByUser = outputByUser.replaceAll("\n", "");
            if (!outputByAnswer.equals(outputByUser)) {
                judgeInfoMessage = JudgeInfoMessageEnum.WRONG_ANSWER;
                // 设置消息
                judgeInfoResponse.setMessage(judgeInfoMessage.getValue());
                return judgeInfoResponse;

            }
        }

        // 判断题目的限制【时间、内存】
        // 答案[标准答案]
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        Long needTimeLimit = judgeConfig.getTimeLimit();


        if (memory > needMemoryLimit) {
            judgeInfoMessage = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            // 设置消息
            judgeInfoResponse.setMessage(judgeInfoMessage.getValue());
            return judgeInfoResponse;

        }


        // Java程序本身需要额外执行10秒钟
        long JAVA_PROGRAM_TIME_COST = 10000L;
        if ((time - JAVA_PROGRAM_TIME_COST) > needTimeLimit) {
            judgeInfoMessage = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            // 设置消息
            judgeInfoResponse.setMessage(judgeInfoMessage.getValue());
            return judgeInfoResponse;

        }


        // 设置消息
        judgeInfoResponse.setMessage(judgeInfoMessage.getValue());
        return judgeInfoResponse;

    }
}
