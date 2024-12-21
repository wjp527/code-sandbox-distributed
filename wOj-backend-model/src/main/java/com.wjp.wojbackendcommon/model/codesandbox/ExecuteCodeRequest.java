package com.wjp.wojbackendcommon.model.codesandbox;

import lombok.*;

import java.util.List;

@Data // getter setter toString 等方法的自动生成
@Builder // 构造器的写法创造对象
@NoArgsConstructor // 无参构造器
@AllArgsConstructor // 全参构造器
public class ExecuteCodeRequest {
    /**
     * 输入用例
     */

    private List<String> inputList;

    /**
     * 代码
     */
    private String code;

    /**
     * 程序语言
     */
    private String language;

}
