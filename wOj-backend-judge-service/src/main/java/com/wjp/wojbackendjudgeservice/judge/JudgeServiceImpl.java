package com.wjp.wojbackendjudgeservice.judge;

import cn.hutool.json.JSONUtil;

import com.wjp.wojbackendcommon.common.ErrorCode;
import com.wjp.wojbackendcommon.exception.BusinessException;
import com.wjp.wojbackendcommon.model.codesandbox.ExecuteCodeReponse;
import com.wjp.wojbackendcommon.model.codesandbox.ExecuteCodeRequest;
import com.wjp.wojbackendcommon.model.codesandbox.JudgeInfo;
import com.wjp.wojbackendcommon.model.dto.question.JudgeCase;
import com.wjp.wojbackendcommon.model.entity.Question;
import com.wjp.wojbackendcommon.model.entity.QuestionSubmit;
import com.wjp.wojbackendcommon.model.enums.JudgeInfoMessageEnum;
import com.wjp.wojbackendcommon.model.enums.QuestionSubmitStatusEnum;
import com.wjp.wojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.wjp.wojbackendjudgeservice.judge.codesandbox.CodeSandboxFactory;
import com.wjp.wojbackendjudgeservice.judge.codesandbox.CodeSandboxProxy;
import com.wjp.wojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.wjp.wojbackendjudgeservice.judge.strategy.JudgeContext;
import com.wjp.wojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionFeignClient questionFeignClient;
 

    @Resource
    private CodeSandbox codeSandbox;

    /**
     * 获取 pom中定义的codesandbox的数据
     */
    @Value("${codesandbox.type:example}")
    private String type;

    @Resource
    private JudgeManager judgeManager;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1、传输题目的提交id，获取到对应的题目、提交信息(包含代码、编程语言等)
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);

        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目提交不存在");
        }

        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        // 2、如果判题状态不为等待中，就不用重复执行
        Integer status = questionSubmit.getStatus();
        if(!status.equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }

        // 3、更改判题(题目提交)状态为"判题中"，防止重复执行，也能让用户即使看到状态
        // Long id = questionSubmit.getId();
        // 更改为 "判题中" 的状态
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
//        questionSubmit.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());

        // questionSubmitUpdate.setStatus(status);

        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if(!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新题目提交状态失败");
        }

        // 4、调用沙箱，获取执行结果
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取输入用例
        String judgeCase = question.getJudgeCase();
        // 转为json数组
        // JSONUtil.toList(): 是将json字符串转为json数组
        // JSONUtil.toList(judgeCase, JudgeCase.class): 将json字符串转为JudgeCase对象列表
        // JSONUtil.toJsonStr(list): 将JudgeCase对象列表转为json字符串
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCase, JudgeCase.class);
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        // 就是将JudgeCase中的input属性全部都装入list集合中
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());

        CodeSandboxProxy codeSandboxProxy = new CodeSandboxProxy(codeSandbox);
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest = executeCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();

        ExecuteCodeReponse executeCodeReponse = codeSandbox.executeCode(executeCodeRequest);


        // 5、根据沙箱的执行结果，设置题目的判题状态和信息
        DefaultJudgeStrategy defaultJudgeStrategy = new DefaultJudgeStrategy();

        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(executeCodeReponse.getOutputList());
        judgeContext.setJudgeInfo(executeCodeReponse.getJudgeInfo());
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);



        // 代码沙箱最终返回的数据
        // 根据语言来进行动态的切换策略模式
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        // 更改为 "判题中" 的状态
//        questionSubmit.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());

        // 6、修改数据中的判题信息
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);


        if(JudgeInfoMessageEnum.ACCEPTED.getValue().equals(judgeInfo.getMessage())) {
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCESS.getValue());

        } else if(JudgeInfoMessageEnum.Waiting.getValue().equals(judgeInfo.getMessage())){
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        } else {
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
        }

        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if(!update) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新题目提交状态失败");
        }

        QuestionSubmit questionSubmitResult = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        return questionSubmitResult;
    }
}
