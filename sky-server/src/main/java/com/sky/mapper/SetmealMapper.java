package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);


    @AutoFill(value = OperationType.INSERT)
    void addSetmeal(Setmeal setmeal);

    Page<Setmeal> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    // 查找所有套餐的status
    List<Integer> getAllStatus(List<Long>ids);

    void deleteSetmealsByIds(List<Long> ids);

    @Select("select * from setmeal where id =#{id} ")
    Setmeal getSetmealById(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void updateById(Setmeal setmeal);


    List<Setmeal> list(Setmeal setmeal);
}
