package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderVO detail(Long id);

    void cancelOrder(Long id);

    void repetition(Long id);

    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderVO detailByAdmin(Long id);

    OrderStatisticsVO OrderStatistics();

    void confirmOrder(OrdersConfirmDTO ordersConfirmDTO);

    void rejectionOrder(OrdersRejectionDTO ordersRejectionDTO);

    void cancelOrderFromAdmin(OrdersCancelDTO ordersCancelDTO);

    void deliveryOrder(Long id);

    void completeOrder(Long id);

    void reminder(Long id);
}
