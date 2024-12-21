package com.wjp.wojbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.wjp.wojbackendcommon.common.ErrorCode;
import com.wjp.wojbackendcommon.constant.CommonConstant;
import com.wjp.wojbackendcommon.exception.BusinessException;
import com.wjp.wojbackendcommon.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.wjp.wojbackendcommon.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.wjp.wojbackendcommon.model.entity.Question;
import com.wjp.wojbackendcommon.model.entity.QuestionSubmit;
import com.wjp.wojbackendcommon.model.entity.User;
import com.wjp.wojbackendcommon.model.enums.QuestionSubmitLanguageEnum;
import com.wjp.wojbackendcommon.model.enums.QuestionSubmitStatusEnum;
import com.wjp.wojbackendcommon.model.vo.QuestionSubmitVO;
import com.wjp.wojbackendcommon.utils.SqlUtils;
import com.wjp.wojbackendquestionservice.manager.RedisLimiterManager;
import com.wjp.wojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.wjp.wojbackendquestionservice.rabbitmq.MyMessageProducer;
import com.wjp.wojbackendquestionservice.service.QuestionService;
import com.wjp.wojbackendquestionservice.service.QuestionSubmitService;
import com.wjp.wojbackendserviceclient.service.JudgeFeignClient;
import com.wjp.wojbackendserviceclient.service.UserFeignClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author wjp
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2024-12-10 20:23:58
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;


    @Resource
    private UserFeignClient userService;

    @Resource
    @Lazy
    private JudgeFeignClient judgeFeignClient;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    /**
     * 消息队列 生产者【用于发送消息】
     */
    @Resource
    private MyMessageProducer myMessageProducer;

    /**
     * 题目提交
     *
     * @param QuestionSubmitVOAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest QuestionSubmitVOAddRequest, User loginUser) {

        // 判断编程语言是否合法
        String language = QuestionSubmitVOAddRequest.getLanguage();
        // 根据字符串获取枚举值
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言不合法");
        }


        // 判断实体是否存在，根据类别获取实体
        Long questionId = QuestionSubmitVOAddRequest.getQuestionId();
        System.out.println("questionId = " + questionId);
        Question question = questionService.getById(questionId);
        System.out.println(question);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已点赞
        long userId = loginUser.getId();
        // 每个用户串行点赞
//        // 锁必须要包裹住事务方法
//        // 可以使用【限流】
//        QuestionSubmitService QuestionSubmitVOService = (QuestionSubmitService) AopContext.currentProxy();
//        synchronized (String.valueOf(userId).intern()) {
//            return QuestionSubmitVOService.doQuestionSubmitInner(userId, questionId);
//        }


        String code = QuestionSubmitVOAddRequest.getCode();
        if (language == null || code == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言或代码为空");
        }
        // 保存提交记录
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setLanguage(language);
        questionSubmit.setCode(code);

        // todo 设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");

        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存失败");
        }

        Long questionSubmitId = questionSubmit.getId();

        // 限流
        redisLimiterManager.doRateLimit("genQuestionSubmitById_" + questionId);
        // 执行判题服务
        myMessageProducer.sendMessage("code_exchange","my_routingKey", String.valueOf(questionSubmitId));

//        CompletableFuture.runAsync(() -> {
//            judgeFeignClient.doJudge(questionSubmitId);
//        });


        return questionSubmitId;
    }

    /**
     * 获取查询包装类【用户可能根据某个字段进行查询】
     *
     * @param questionSubmitVOQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitVOQueryRequest) {

        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitVOQueryRequest == null) {
            return queryWrapper;
        }

        String language = questionSubmitVOQueryRequest.getLanguage();
        Integer status = questionSubmitVOQueryRequest.getStatus();
        Long questionId = questionSubmitVOQueryRequest.getQuestionId();
        Long userId = questionSubmitVOQueryRequest.getUserId();
        String sortField = questionSubmitVOQueryRequest.getSortField();
        String sortOrder = questionSubmitVOQueryRequest.getSortOrder();


        // 拼接查询条件

        queryWrapper.like(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.like(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.like(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题目封装
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        long userId = loginUser.getId();
        // 处理脱敏
        if (userId != questionSubmit.getUserId() && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    /**
     * 分页获取提交封装类
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }


}




