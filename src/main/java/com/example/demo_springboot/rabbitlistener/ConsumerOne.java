package com.example.demo_springboot.rabbitlistener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@RabbitListener(bindings = {
        @QueueBinding(
                value = @Queue(
                    name = "test.fanout.queue"
                ),
                exchange = @Exchange(
                        name = "test.exchange.fanout",
                        type = ExchangeTypes.FANOUT
                ),
                key = "fanout.test"
        )
})
@Component
@Slf4j
public class ConsumerOne {

    @RabbitHandler
    public void receive(String msg) {
        log.info("ConsumerOne--->" + msg);
    }
}
