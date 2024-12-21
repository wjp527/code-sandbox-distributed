package com.wjp.wojbackendquestionservice.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wjp.wojbackendcommon.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.wjp.wojbackendcommon.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.wjp.wojbackendcommon.model.entity.QuestionSubmit;
import com.wjp.wojbackendcommon.model.entity.User;
import com.wjp.wojbackendcommon.model.vo.QuestionSubmitVO;

/**
 * @author wjp
 * @description 针对表【question_submit(题目提交)】的数据库操作Service
 * @createDate 2024-12-10 20:23:58
 */
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    /**
     * 点赞
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return 条件记录的id
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);


    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);

}
