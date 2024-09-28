package com.sky.mapper;


import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    void insert(Orders orders);

//    Page<OrderVO> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);


    List<Orders> getAllByUserId(OrdersPageQueryDTO ordersPageQueryDTO);


    List<Orders> list(Orders orders);

    void update(Orders orders);

//    订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
    @Select("select count(*) from orders where status = #{confirmed} ")
    Integer confirmed(Integer confirmed);

    @Select("select count(*) from orders where status = #{deliveryInProgress}")
    Integer deliveryInProgress(Integer deliveryInProgress);

    @Select("select count(*) from orders where status = #{toBeConfirmed}")
    Integer toBeConfirmed(Integer toBeConfirmed);

    List<Orders> getAllFuzzySearch(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select  * from orders where status =#{status} and order_time <#{orderTime}")
    List<Orders> getBystatusAndOrderTime(Integer status, LocalDateTime orderTime);

    @Select("select * from orders where id =#{id}")
    Orders getByid(Long id);

    Double sumByMap(Map map);

    Integer getCurrentOrderNum(Map map);


    List<GoodsSalesDTO> getSalesTop(LocalDateTime begin, LocalDateTime end);
}
