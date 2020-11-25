package com.atguigu.springcloud.service.impl;

import com.atguigu.springcloud.dao.OrderDao;
import com.atguigu.springcloud.domain.Order;
import com.atguigu.springcloud.service.AccountService;
import com.atguigu.springcloud.service.OrderService;
import com.atguigu.springcloud.service.StorageService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private AccountService accountService;

    @Autowired
    private StorageService storageService;

    @Override
    @GlobalTransactional(name = "fsp-create-order",rollbackFor = Exception.class)
    public void create(Order order) {
        log.info("====>新建订单");
        orderDao.create(order);
        log.info("创建订单结束");

        log.info("=====>库存减");
        storageService.decrease(order.getProductId(),order.getCount());
        log.info("库存减结束");

        log.info("====>扣钱");
        accountService.decrease(order.getUserId(),order.getMoney());
        log.info("扣钱结束");

        log.info("修改订单状态");
        orderDao.update(order.getUserId(),0);
        log.info("修改订单状态结束");

        log.info("呵呵");

    }
}
