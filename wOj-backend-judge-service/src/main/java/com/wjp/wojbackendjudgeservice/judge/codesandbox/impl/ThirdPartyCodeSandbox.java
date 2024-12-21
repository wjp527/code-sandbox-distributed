package com.wjp.wojbackendjudgeservice.judge.codesandbox.impl;


import com.wjp.wojbackendcommon.model.codesandbox.ExecuteCodeReponse;
import com.wjp.wojbackendcommon.model.codesandbox.ExecuteCodeRequest;
import com.wjp.wojbackendjudgeservice.judge.codesandbox.CodeSandbox;

/**
 * 第三方服务代码沙箱【调用网上线程的代码沙箱】
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeReponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方服务代码沙箱");
        return null;
    }
}
