package com.wjp.wojbackendjudgeservice.rabbitmq;

import com.rabbitmq.client.Channel;
import com.wjp.wojbackendjudgeservice.judge.JudgeService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MyMessageConsumer {

    @Resource
    private JudgeService judgeService;

    @SneakyThrows
    // queues = {"code_queue"}: 监听队列名为code_queue的消息
    // ackMode: 设置为MANUAL，表示手动确认消息，消息消费者需要调用channel.basicAck()来确认消息被消费
    @RabbitListener(queues = {"code_queue"}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("ReceivedMessage message: {}",message);

        long questionSubmitId = Long.parseLong(message);

        try {
            judgeService.doJudge(questionSubmitId);
            // 只要消息被消费，就ack
            channel.basicAck(deliveryTag, false);
        } catch(Exception e) {
            // 出现异常，则nack
            log.error("Judge failed, message: {}, error: {}", message, e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }


    }

}
