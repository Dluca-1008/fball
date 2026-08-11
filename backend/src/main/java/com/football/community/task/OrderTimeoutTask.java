package com.football.community.task;

import com.football.community.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderTimeoutTask {

    @Autowired
    private OrderService orderService;

    @Scheduled(fixedRate = 60000)
    public void processExpiredOrders() {
        log.debug("定时任务: 检查过期订单");
        orderService.cancelExpiredOrders();
    }
}
