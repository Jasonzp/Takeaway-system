package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    // 新增菜品的方法
    public void savewithflavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void deleteById(List<Long> ids);

    void updateById(DishDTO dishDTO);

    DishVO getByIdwithFlavor(Long id);

    List<Dish> getAllByCategoryId(Long categoryId);

    void updateStatusById(Integer status, Long id);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
