package com.sky.controller.admin;


import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

// 设定为 Controller 层
@RestController
// 设定请求总路径
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {

    @Autowired
    SetmealService setmealService;

    @PostMapping
    @ApiOperation("新增加套餐")
    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")
    public Result addSetmeal(@RequestBody SetmealDTO setmealDTO) {
        setmealService.addSetmeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分页查询")
    Result<PageResult>  pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageresult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageresult);
    }

    @DeleteMapping
    @ApiOperation("删除套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)  // 直接清理所有的缓存数据
    public Result deleteSetmeal(@RequestParam List<Long> ids) {

        setmealService.deleteSetmealByIds(ids);
        return Result.success();

    }
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result getSetmeal(@PathVariable Long id) {

        SetmealVO setmeal = setmealService.getSetmealById(id);

        return Result.success(setmeal);
    }

    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)  // 直接清理所有的缓存数据
    public Result updateSetmeal(@RequestBody SetmealDTO setmealDTO) {
        setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售停售")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)  // 直接清理所有的缓存数据
    public Result updateSetmealStatus(@PathVariable Integer status, Long id) {
        setmealService.updateSetmealStatus(status,id);
        return Result.success();
    }
}
