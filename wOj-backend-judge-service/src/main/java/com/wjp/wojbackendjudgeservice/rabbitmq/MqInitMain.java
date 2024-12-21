package com.wjp.wojbackendjudgeservice.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 初始化消息队列
 */
@Slf4j
public class MqInitMain {
    public static void doInit(){

        try {
            // 创建连接工厂
            ConnectionFactory factory = new ConnectionFactory();

            // 设置 RabbitMQ 服务器地址
            factory.setHost("localhost");
            // 从工厂中创建连接
            Connection connection = factory.newConnection();

            // 创建通道
            Channel channel = connection.createChannel();

            // 定义交换机名称
            String EXCHANGE_NAME = "code_exchange";

            // 声明交换机 类型为 direct
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            // 创建队列
            String queueName = "code_queue";
            // 创建队列 可持久化存储 消息
            channel.queueDeclare(queueName, true, false, false, null);
            // 绑定队列到交换机
            channel.queueBind(queueName, EXCHANGE_NAME, "my_routingKey");

            log.info("初始化消息队列成功");

        } catch (Exception e) {
            log.error("初始化消息队列失败");
        }
    }
    public static void main(String[] args) {
        doInit();
    }
}
