package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    List<Long> getSetmealDishIdsByDishId(List<Long> dishIds);

    void deleteSetmeal_DishBySetmealIds(List<Long> SetmealIds);


    void insertBatch(List<SetmealDish> setmealDishes);

    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void updateSetmealById(Setmeal setmeal);
}
