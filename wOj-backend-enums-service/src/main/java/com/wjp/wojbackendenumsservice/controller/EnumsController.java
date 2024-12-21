package com.wjp.wojbackendenumsservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.wjp.wojbackendcommon.common.BaseResponse;
import com.wjp.wojbackendcommon.common.ErrorCode;
import com.wjp.wojbackendcommon.common.PageRequest;
import com.wjp.wojbackendcommon.common.ResultUtils;
import com.wjp.wojbackendcommon.exception.ThrowUtils;
import com.wjp.wojbackendcommon.model.enums.QuestionSubmitLanguageEnum;
import com.wjp.wojbackendcommon.model.vo.EnumsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 帖子接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://wjp.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/")
@Slf4j
public class EnumsController {
 


    // region 增删改查


    /**
     * 分页获取列表（封装类）
     *
     * @param pageRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<EnumsVO>> listEnumsVOByPage(@RequestBody PageRequest pageRequest,
                                                         HttpServletRequest request) {
        long current = pageRequest.getCurrent();
        long size = pageRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        List<String> values = QuestionSubmitLanguageEnum.getValues();

        // 定义一个map集合，存放枚举值和枚举名称
        AtomicReference<Long> id = new AtomicReference<>(1L);
        List<EnumsVO> collect = values.stream().map(item -> {
            EnumsVO enumsVO = new EnumsVO();
            enumsVO.setId(id.getAndSet(id.get() + 1));
            enumsVO.setValue(item);
            enumsVO.setLabel(item);
            return enumsVO;
        }).collect(Collectors.toList());

        // 进行分页
        Page<EnumsVO> page = new Page<>(current, size);
        long total = collect.size();
        int start = (int) ((current - 1) * size);
        int end = (int) Math.min(start + size, total);
        List<EnumsVO> pageRecords = collect.subList(start, end);

        page.setRecords(pageRecords);
        page.setTotal(total);
        page.setSize(size);
        page.setCurrent(current);


        return ResultUtils.success(page);
    }



}
