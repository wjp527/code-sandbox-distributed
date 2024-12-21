package com.wjp.wojbackendserviceclient.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wjp.wojbackendcommon.model.dto.question.QuestionQueryRequest;
import com.wjp.wojbackendcommon.model.entity.Question;
import com.wjp.wojbackendcommon.model.entity.QuestionSubmit;
import com.wjp.wojbackendcommon.model.vo.QuestionVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wjp
 * @description 针对表【question(题目)】的数据库操作Service
 * @createDate 2024-12-10 20:23:58
 */
@FeignClient(name = "wOj-backend-question-service", path = "/api/question/inner")
public interface QuestionFeignClient   {

    @GetMapping("/get/id")
    Question getQuestionById(@RequestParam("questionId") long questionId);

    @GetMapping("/question_submit/get/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId);


    @PostMapping("/question_submit/update")
    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit);

}
