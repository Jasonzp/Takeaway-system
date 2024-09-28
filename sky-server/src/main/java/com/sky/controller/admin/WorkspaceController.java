package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@Slf4j
@Api(tags = "工作台相关接口")
@RequestMapping("/admin/workspace")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @GetMapping("/businessData")
    @ApiOperation("查询今日运营数据")
    public Result<BusinessDataVO> getBusinessData() {

        // 创建当前的时间
        LocalDate currentDate = LocalDate.now();
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(currentDate,currentDate);

        return Result.success(businessDataVO);
    }

    @GetMapping("/overviewOrders")
    @ApiOperation("查询订单管理数据")
    public Result<OrderOverViewVO> getOverviewOrders() {
        OrderOverViewVO orderOverViewVO = workspaceService.getOverviewOrders();

        return Result.success(orderOverViewVO);
    }

    @GetMapping("/overviewSetmeals")
    @ApiOperation("查询套餐总览")
    public Result<SetmealOverViewVO> getOverviewSetmeals() {
        SetmealOverViewVO setmealOverViewVO = workspaceService.getOverviewSetmeals();

        return Result.success(setmealOverViewVO);
    }

    @GetMapping("/overviewDishes")
    @ApiOperation("查询菜品总览")
    public Result<DishOverViewVO> getOverviewDishes() {
        DishOverViewVO dishOverViewVO = workspaceService.getOverviewDishes();
        return Result.success(dishOverViewVO);
    }

}
