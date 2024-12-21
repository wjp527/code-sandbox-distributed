package com.wjp.wojbackendcommon.model.vo;

import cn.hutool.json.JSONUtil;
import com.wjp.wojbackendcommon.model.codesandbox.JudgeInfo;
import com.wjp.wojbackendcommon.model.entity.Question;
import com.wjp.wojbackendcommon.model.entity.QuestionSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目封装类
 */
@Data
public class QuestionSubmitVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 语言
     */
    private String language;

    /**
     * 代码
     */
    private String code;

    /**
     * 判题状态(0-待判题、1-判题中、2-成功、3-失败)
     */
    private Integer status;

    /**
     * 判题信息(json)
     */
    private JudgeInfo judgeInfo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 提交者信息
     */
    private UserVO userVO;

    /**
     * 题目信息
     */
    private QuestionVO questionVO;

    /**
     * 包装类转对象
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionSubmitVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);

        JudgeInfo judgeInfo = questionVO.getJudgeInfo();
        if (judgeInfo != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeInfo));
        }
        return question;
    }

    /**
     * 对象转包装类
     *
     * @param questionSubmit
     * @return
     */
    public static QuestionSubmitVO objToVo(QuestionSubmit questionSubmit) {
        if (questionSubmit == null) {
            return null;
        }
        QuestionSubmitVO questionVO = new QuestionSubmitVO();
        BeanUtils.copyProperties(questionSubmit, questionVO);
        JudgeInfo judgeInfo = JSONUtil.toBean(questionSubmit.getJudgeInfo(), JudgeInfo.class);
        questionVO.setJudgeInfo(judgeInfo);
        return questionVO;
    }

    private static final long serialVersionUID = 1L;
}
