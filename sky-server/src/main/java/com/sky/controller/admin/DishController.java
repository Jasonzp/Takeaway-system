package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
//    @Autowired
//    private DishMapper dishMapper;
    @Autowired
    private RedisTemplate redisTemplate;


    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品:{}",dishDTO);
        dishService.savewithflavor(dishDTO);

        // 清理缓存数据
        String key ="dish_"+dishDTO.getCategoryId();
        redisTemplate.delete(key);

        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(  DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageresult = dishService.pageQuery(dishPageQueryDTO);

        return Result.success(pageresult);
    }

    // 希望由MVC框架动态解析字符串
    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("删除/批量删除菜品:{}",ids);
        dishService.deleteById(ids);

        // 将所有的菜品缓存数据清理掉，所有以dish_开头的key
        cleanCache("dish_*");
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);

        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        dishService.updateById(dishDTO);

        // 将所有的菜品缓存数据清理掉，所有以dish_开头的key
        cleanCache("dish_*");
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);

        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id找菜品")
    public Result<DishVO> getById(@PathVariable Long id) {

        DishVO dishVO = dishService.getByIdwithFlavor(id);
        return Result.success(dishVO);
    }

    // 根据分类id查询菜品
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> getAll(Long categoryId ) {
        List<Dish> ans = dishService.getAllByCategoryId(categoryId);
        return Result.success(ans);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result updateStatus(@PathVariable Integer status, Long id) {
        dishService.updateStatusById(status,id);

        // 将所有的菜品缓存数据清理掉，所有以dish_开头的key
        cleanCache("dish_*");
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);

        return Result.success();

    }

    /*
    * 清理缓存数据
    * */
    private void cleanCache(String pattern)
    {
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);

    }

}
