package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
   private OrderMapper orderMapper;
    @Autowired
   private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private WebSocketServer webSocketServer;


    // 用户下订单
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

        // 处理各种业务异常（地址为空，购物车数据为空）
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        // 查询当前的购物车
        ShoppingCart shoppingCart = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);

        if(shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        // 向订单表插入1条数据
        Orders orders =new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);

        String address = addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail();
        orders.setAddress(address);

        orderMapper.insert(orders);

        List<OrderDetail> orderDetailList = new ArrayList<>();
        // 向订单明细表插入n条数据
        for (ShoppingCart cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart,orderDetail);
            orderDetail.setOrderId(orders.getId()); // 设置当前订单明细关联的订单id
            orderDetailList.add(orderDetail);
        }

        orderDetailMapper.insertBatch(orderDetailList);
        
        // 清空当前用户的购物车数据
        ShoppingCart shoppingCart1 = new ShoppingCart();
        shoppingCart1.setUserId(userId);
        shoppingCartMapper.clean(shoppingCart1);

        // 封装VO 返回数据
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();

        return  orderSubmitVO;
    }

    @Override
    @Transactional
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        List<Orders> ordersList = orderMapper.getAllByUserId(ordersPageQueryDTO);

        Page<OrderVO> page = new Page<>();

        for (Orders orders : ordersList) {
            List<OrderDetail> orderDetailList = orderDetailMapper.getAllByOrderId(orders.getId());
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders,orderVO);
            orderVO.setOrderDetailList(orderDetailList);
            page.add(orderVO);
        }
        

//        OrderVO orderVO = new OrderVO();
//        // 复制
//        BeanUtils.copyProperties(ordersPageQueryDTO, orderVO);
//
//        List<OrderDetail> orderDetailsList = orderDetailMapper.getAllByUserId(orderVO.getUserId());

//        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional
    public OrderVO detail(Long id) {
        Orders orders = new Orders();
//        设置订单id
        orders.setId(id);
        // 设置用户id
        orders.setUserId(BaseContext.getCurrentId());
        List<Orders> ordersList = orderMapper.list(orders);

        orders = ordersList.get(0);
        List<OrderDetail> orderDetailList = orderDetailMapper.getAllByOrderId(id);

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;

    }

    @Override
    public void cancelOrder(Long id) {
        Orders orders = new Orders();
        orders.setId(id);
        orders.setUserId(BaseContext.getCurrentId());

        // 支付状态
        orders.setPayStatus(Orders.UN_PAID);
        // 订单状态
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());


       orderMapper.update(orders);



    }

    @Override
    @Transactional
    public void repetition(Long id) {
        // 查询当前用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单id查询当前订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getAllByOrderId(id);

        // 将订单详情对象转换为购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            // 将原订单详情里面的菜品信息重新复制到购物车对象中
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        // 将购物车对象批量添加到数据库
        for (ShoppingCart shoppingCart : shoppingCartList) {
            shoppingCartMapper.insert(shoppingCart);
        }
//        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    @Override
    @Transactional
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        List<Orders> ordersList = orderMapper.getAllFuzzySearch(ordersPageQueryDTO);

        Page<OrderVO> page = new Page<>();
        for (Orders orders : ordersList) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders,orderVO);
            List<OrderDetail> orderDetailList = orderDetailMapper.getAllByOrderId(orders.getId());
            String orderDishes ="";
            for (OrderDetail orderDetail : orderDetailList) {

                if (orderDetail.getDishId() != null) {
                    Long dishId = orderDetail.getDishId();
                    String dishName = dishMapper.getNameByDishId(dishId);
                    orderDishes += dishName + " ";
                } else if (orderDetail.getSetmealId() != null) {
                    Long setmealId = orderDetail.getSetmealId();
                    Setmeal setmeal = setmealMapper.getSetmealById(setmealId);
                    String setmealName = setmeal.getName();
                    orderDishes += setmealName + " ";
                }
            }
            orderVO.setOrderDishes(orderDishes);
            page.add(orderVO);
        }
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional
    public OrderVO detailByAdmin(Long id) {
        Orders orders = new Orders();
//        设置订单id
        orders.setId(id);
        // 设置用户id
//        orders.setUserId(BaseContext.getCurrentId());
        List<Orders> ordersList = orderMapper.list(orders);

        orders = ordersList.get(0);
        List<OrderDetail> orderDetailList = orderDetailMapper.getAllByOrderId(id);

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }

    @Override
    @Transactional
    public OrderStatisticsVO OrderStatistics() {
        /**
         * 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
         */
//        public static final Integer PENDING_PAYMENT = 1;
//        public static final Integer TO_BE_CONFIRMED = 2;
//        public static final Integer CONFIRMED = 3;
//        public static final Integer DELIVERY_IN_PROGRESS = 4;
//        public static final Integer COMPLETED = 5;
//        public static final Integer CANCELLED = 6;



        Integer confirmed  = orderMapper.confirmed(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.deliveryInProgress(Orders.DELIVERY_IN_PROGRESS);
        Integer toBeConfirmed =orderMapper.toBeConfirmed(Orders.TO_BE_CONFIRMED);

        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        return orderStatisticsVO;
    }

    @Override
    public void confirmOrder(OrdersConfirmDTO ordersConfirmDTO) {
        Orders orders = new Orders();
        orders.setId(ordersConfirmDTO.getId());
        ordersConfirmDTO.setStatus(Orders.CONFIRMED);
        orders.setStatus(ordersConfirmDTO.getStatus());

        orderMapper.update(orders);
    }

    @Override
    public void rejectionOrder(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = new Orders();
        orders.setId(ordersRejectionDTO.getId());
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setStatus(Orders.CANCELLED);

        orderMapper.update(orders);
    }

    @Override
    public void cancelOrderFromAdmin(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = new Orders();
        // 订单取消原因    //订单取消时间
        // private String cancelReason;  private LocalDateTime cancelTime;
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);

        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());

        orderMapper.update(orders);


    }

    @Override
    public void deliveryOrder(Long id) {
        Orders orders = new Orders();
//        /**
//         * 订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
//         */
//        public static final Integer PENDING_PAYMENT = 1;
//        public static final Integer TO_BE_CONFIRMED = 2;
//        public static final Integer CONFIRMED = 3;
//        public static final Integer DELIVERY_IN_PROGRESS = 4;
//        public static final Integer COMPLETED = 5;
//        public static final Integer CANCELLED = 6;
        orders.setId(id);
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);


    }

    @Override
    public void completeOrder(Long id) {
        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(Orders.COMPLETED);
        orderMapper.update(orders);

    }

    @Override
    public void reminder(Long id) {

        Orders orders = new Orders();

        orders = orderMapper.getByid(id);

        Map map = new HashMap();
        map.put("type", 2); // 1. 表示来单提醒 2 表示客户催单
        map.put("orderId", id);
        map.put("content","订单号："+orders.getNumber());

        // 通过webSocket 向后台推消息
        webSocketServer.sendToAllClient(JSON.toJSONString(map));


    }

}

