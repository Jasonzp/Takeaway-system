package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/order")
@Api(tags = "订单接口")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    Result<OrderVO> details(@PathVariable Long id) {
        OrderVO orderVO = orderService.detailByAdmin(id);
        return Result.success(orderVO);
    }

    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {

        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单状态")
    Result<OrderStatisticsVO> OrderStatistics() {
        OrderStatisticsVO orderStatisticsVO = orderService.OrderStatistics();
        return Result.success(orderStatisticsVO);
    }

    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirmOrder(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        orderService.confirmOrder(ordersConfirmDTO);
        return Result.success();
    }

    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejectionOrder(@RequestBody OrdersRejectionDTO ordersRejectionDTO) {
//        rejectionReason
        orderService.rejectionOrder(ordersRejectionDTO);
        return Result.success();
    }

    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancelOrder(@RequestBody OrdersCancelDTO ordersCancelDTO) {
        // Orders
        // 订单取消原因    //订单取消时间
        // private String cancelReason;  private LocalDateTime cancelTime;
        orderService.cancelOrderFromAdmin(ordersCancelDTO);
        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result deliveryOrder(@PathVariable Long id) {

        orderService.deliveryOrder(id);
        return Result.success();
    }
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result completeOrder(@PathVariable Long id) {
        orderService.completeOrder(id);

        return Result.success();
    }

}
