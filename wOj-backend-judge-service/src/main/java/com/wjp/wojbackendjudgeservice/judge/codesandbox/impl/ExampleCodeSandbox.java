package com.wjp.wojbackendjudgeservice.judge.codesandbox.impl;

import com.wjp.wojbackendcommon.model.codesandbox.ExecuteCodeReponse;
import com.wjp.wojbackendcommon.model.codesandbox.ExecuteCodeRequest;
import com.wjp.wojbackendcommon.model.codesandbox.JudgeInfo;
import com.wjp.wojbackendcommon.model.enums.JudgeInfoMessageEnum;
import com.wjp.wojbackendcommon.model.enums.QuestionSubmitStatusEnum;
import com.wjp.wojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 示例代码沙箱【示例: 仅为了跑通项目流程】
 */
@Service
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeReponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();

        ExecuteCodeReponse executeCodeReponse = new ExecuteCodeReponse();
        executeCodeReponse.setOutputList(inputList);
        executeCodeReponse.setMessage("测试执行成功");
        executeCodeReponse.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());

        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);

        executeCodeReponse.setJudgeInfo(judgeInfo);




        return executeCodeReponse;
    }
}
