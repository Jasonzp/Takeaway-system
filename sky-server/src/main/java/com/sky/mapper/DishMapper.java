package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {

    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

//    @Insert("insert into dish (id,name,category_id,price,image,description,status,create_time,update_time,create_user,update_user)"+
//            "values (#{id},#{name},#{categoryId},#{price},#{image},#{description}.#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void deleteById(List<Long> ids);

    @Select("select * from dish where id =#{id}")
    Dish getById(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void updateById(Dish dish);

    @Select("select * from dish where category_id =#{categoryId};")
    List<Dish> getAllByCategoryId(Long categoryId);

    List<Dish> list(Dish dish);

    @Select("select name from dish where id=#{id}")
    String getNameByDishId(Long id);


//    void updateStatusById(Dish dish);
}
