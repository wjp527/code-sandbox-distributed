package com.wjp.wojbackendjudgeservice.judge.codesandbox;


import com.wjp.wojbackendcommon.model.codesandbox.ExecuteCodeReponse;
import com.wjp.wojbackendcommon.model.codesandbox.ExecuteCodeRequest;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandbox {
    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeReponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
