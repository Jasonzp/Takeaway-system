package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Api(tags = "C端订单接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);

        return Result.success(orderSubmitVO);
    }

    @GetMapping("/historyOrders")
    @ApiOperation("历史订单查询")
    Result<PageResult>  pageQuery(OrdersPageQueryDTO ordersPageQueryDTO){
        // 设置用户userId
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        PageResult pageresult = orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageresult);
    }

    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public  Result<OrderVO> detail(@PathVariable Long id){
        OrderVO  orderVO = orderService.detail(id);

        return Result.success(orderVO);
    }

    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result cancelOrder(@PathVariable Long id){
        orderService.cancelOrder(id);
        return Result.success();
    }

    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id){

        orderService.repetition(id);
        return Result.success();
    }

    // 催单
    @GetMapping("/reminder/{id}")
    @ApiOperation("用户催单")
    public  Result reminder(@PathVariable("id") Long id){
        orderService.reminder(id);
        return Result.success();
    }

//    @PutMapping("")



}
