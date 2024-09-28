package com.sky.controller.admin;


import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Api(tags = "分类接口")
@Slf4j
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @PostMapping
    @ApiOperation("添加分类")
    public Result addCategory(@RequestBody CategoryDTO categoryDTO) {
        categoryService.save(categoryDTO);

        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("锁定分类")
    public Result updateCategoryStatus(@PathVariable("status") Integer status,Long id) {
        categoryService.startOrStop(status,id);
        return Result.success();

    }

    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    Result<PageResult>  pageQuery(CategoryPageQueryDTO categoryPageQueryDTO){
        PageResult pageresult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageresult);
    }

    @DeleteMapping
    @ApiOperation("删除分类")
    public Result deleteCategory(Long id) {
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result<String> update(@RequestBody CategoryDTO categoryDTO){
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @GetMapping("/list")
    Result<List<Category>> listCategory(Integer type){
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }





}
