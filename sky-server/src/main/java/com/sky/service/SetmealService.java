package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    void addSetmeal(SetmealDTO setmealDTO);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteSetmealByIds(List<Long> ids);

    SetmealVO getSetmealById(Long id);

    void updateSetmeal(SetmealDTO setmealDTO);

    void updateSetmealStatus(Integer status, Long id);


    List<Setmeal> list(Setmeal setmeal);

    List<DishItemVO> getDishItemBySetmealId(Long id);
}
