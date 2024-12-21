package com.wjp.wojbackendquestionservice.manager;

import com.wjp.wojbackendcommon.common.ErrorCode;
import com.wjp.wojbackendcommon.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import org.redisson.client.RedisException;
/**
 * 专门提供 RedisLimiter 限流基础服务(提供了通用的功能)
 */
// 打上@Service注解，将其注入到Spring容器中，可以直接使用
@Service
public class RedisLimiterManager {

    // 1、
    @Resource
    private RedissonClient redissonClient;

    public void doRateLimit(String key) {
        // 2. 创建一个 RRateLimiter 实例
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);

        // 尝试设置限流规则

        // 3. 设置限流规则：每秒最多 1 次
        // 第二个参数: 每个时间单位允许访问几次
        // 第三个参数: 时间单位
        // 第四个参数: 限流类型 (秒？分？毫秒？)
        boolean isRateSet = rateLimiter.trySetRate(RateType.OVERALL, 1, 10, RateIntervalUnit.SECONDS);

        if (!isRateSet) {
            System.out.println("限流规则已存在，未重新设置: key = " + key);
        }

        try {
            // 如果设置的是2个令牌
            // rateLimiter.trySetRate(RateType.OVERALL, 1, 1, RateIntervalUnit.SECONDS); 的的第一个参数就是每个时间段内接受的令牌数, 如果大于总令牌数，则会报错

            // 4. 尝试获取1个令牌，如果获取不到，则抛出异常
            // 这里有个场景就是: 区分VIP和普通用户，VIP用户可以访问的次数更多，但是普通用户只能访问的次数更少，就是这个令牌的数量限制
            // VIP: 设置的少，可以访问的次数更多，但是不能超过2次
            // 普通用户: 设置的多，可以访问的次数更少，但是不能超过2次
            boolean result = rateLimiter.tryAcquire(1);
            if (!result) {
                throw new BusinessException(ErrorCode.TOO_MANY_REQUEST, "访问频率过高，请稍后再试");
            }
        } catch (RedisException e) {
            // 捕获 Redis 脚本错误（例如超过限流配置）
            if (e.getMessage().contains("Requested permits amount cannot exceed defined rate")) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求的令牌数超过限流配置，请检查规则设置");
            }
            // 其他 Redis 相关异常
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统异常，请稍后再试");
        }
    }
}
