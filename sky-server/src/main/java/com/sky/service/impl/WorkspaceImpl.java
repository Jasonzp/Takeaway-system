package com.sky.service.impl;

import com.sky.entity.Dish;
import com.sky.entity.Orders;
import com.sky.entity.Setmeal;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkspaceImpl implements WorkspaceService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;


    @Override
    @Transactional
    public BusinessDataVO getBusinessData(LocalDate begin ,LocalDate end) {



        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        Map map = new HashMap();
        map.put("begin", beginTime);
        map.put("end", endTime);

        // 新增用户数目
        Integer newUserNum = userMapper.sumCurrentDate(map);
        newUserNum = newUserNum == null ? 0 : newUserNum;
        // 全部订单数
        Integer totalOrderNum = orderMapper.getCurrentOrderNum(map);
        totalOrderNum = totalOrderNum == null ? 0 : totalOrderNum;

        // 今天的营业额
        map.put("status", Orders.COMPLETED);
        Double turnover = orderMapper.sumByMap(map);
        turnover = turnover == null ? 0 : turnover;
        // 有效的订单数目
        Integer validOrderCount = orderMapper.getCurrentOrderNum(map);
        validOrderCount = validOrderCount == null ? 0 : validOrderCount;
        // 订单完成率
        double orderCompletionRate;
        if (totalOrderNum != 0) {
            orderCompletionRate = (double) validOrderCount / totalOrderNum;
        } else {
            orderCompletionRate = 0;
        }
        // 客单价格
        double unitPrice;
        if (validOrderCount == 0) {
            unitPrice = 0;
        } else {
            unitPrice = turnover / validOrderCount;
        }


        BusinessDataVO businessDataVO = BusinessDataVO.builder()
                .newUsers(newUserNum)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .validOrderCount(validOrderCount)
                .turnover(turnover).build();


        return businessDataVO;
    }

    @Override
    @Transactional
    public OrderOverViewVO getOverviewOrders() {
        // 创建当前的时间
        LocalDate currentDate = LocalDate.now();
        LocalDateTime beginTime = LocalDateTime.of(currentDate, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(currentDate, LocalTime.MAX);

        Map map = new HashMap();
        map.put("begin", beginTime);
        map.put("end", endTime);

        // 全部订单数目
        Integer allOrders =orderMapper.getCurrentOrderNum(map);
        allOrders = allOrders == null ? 0 : allOrders;

        map.put("status",Orders.CANCELLED);
        // 已经取消的订单数目
        Integer cancelledOrders =orderMapper.getCurrentOrderNum(map);
        cancelledOrders = cancelledOrders == null? 0 : cancelledOrders;
        //已经完成
        map.put("status",Orders.COMPLETED);
        Integer completedOrders =orderMapper.getCurrentOrderNum(map);
        completedOrders = completedOrders == null? 0 : completedOrders;
        //等待派送的订单数量
        map.put("status",Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.getCurrentOrderNum(map);
        deliveredOrders = deliveredOrders == null? 0 : deliveredOrders;
        // 等待接单
        map.put("status",Orders.TO_BE_CONFIRMED);
        Integer waitingOrders = orderMapper.getCurrentOrderNum(map);
        waitingOrders = waitingOrders == null? 0 : waitingOrders;

        OrderOverViewVO orderOverViewVO = OrderOverViewVO.builder()
                .allOrders(allOrders)
                .cancelledOrders(cancelledOrders)
                .completedOrders(completedOrders)
                .deliveredOrders(deliveredOrders)
                .waitingOrders(waitingOrders).build();

        return orderOverViewVO;
    }

    @Override
    @Transactional
    public SetmealOverViewVO getOverviewSetmeals() {

        //状态 0:停用 1:启用
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(0);

        Integer discontinued = 0;

        List<Setmeal> setmealList0 = setmealMapper.list(setmeal);

        if(setmealList0 == null || setmealList0.size() == 0) {
            discontinued = 0;
        }else
        {
            discontinued = setmealList0.size();
        }

        setmeal.setStatus(1);
        Integer sold = 0;
        List<Setmeal> setmealList1 = setmealMapper.list(setmeal);
        if(setmealList1 == null || setmealList1.size() == 0) {
            sold = 0;
        }else
        {
            sold = setmealList1.size();
        }

        SetmealOverViewVO setmealOverViewVO = SetmealOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold).build();
        return setmealOverViewVO;
    }

    @Override
    public DishOverViewVO getOverviewDishes() {

        Integer discontinued = 0;
        Integer sold = 0;

        Dish dish = new Dish();
        //0 停售 1 起售
        dish.setStatus(0);
        List<Dish> dishList = dishMapper.list(dish);
        if(dishList == null || dishList.size() == 0) {
            discontinued = 0;
        }else {
            discontinued = dishList.size();
        }
        dish.setStatus(1);
        List<Dish> dishList1 = dishMapper.list(dish);
        if(dishList1 == null || dishList1.size() == 0) {
            sold = 0;
        }else {
            sold = dishList1.size();
        }

        DishOverViewVO dishOverViewVO = DishOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold).build();

        return dishOverViewVO;
    }
}
