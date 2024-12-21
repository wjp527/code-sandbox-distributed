package com.wjp.wojbackendjudgeservice.judge.codesandbox;


import com.wjp.wojbackendcommon.model.codesandbox.ExecuteCodeReponse;
import com.wjp.wojbackendcommon.model.codesandbox.ExecuteCodeRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码沙箱（静态代理）
 */
@Slf4j
// 之所以要实现 这个接口
// 是因为: 这里要用代理模式，之所以要用代理模式，就是要增强这个接口
public class CodeSandboxProxy implements CodeSandbox{

    /**
     * 引入原始的代码沙箱
     */
    private CodeSandbox codeSandbox;

    /**
     * 构造函数
     * @param codeSandbox
     */
    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecuteCodeReponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱请求信息:" + executeCodeRequest.toString());

        ExecuteCodeReponse executeCodeReponse = codeSandbox.executeCode(executeCodeRequest);

        log.info("代码沙箱返回信息:" + executeCodeReponse.toString());

        return executeCodeReponse;
    }
}
