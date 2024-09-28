package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional
    public void savewithflavor(DishDTO dishDTO) {
        // 涉及两张表的统一性 @Transactional [原子性]
        Dish dish = new Dish();
        // 向菜品表插入一条数据
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);

        // 获取inset语句生成的主键值
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();

        if(flavors != null && flavors.size() > 0){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
                    }
            );

            // 向口味表插入可能插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        // 开始分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional
    public void deleteById(List<Long> ids) {

        // 判断当前菜品是否能够被删除 -- 是否存在起售中的菜品
        for(Long id : ids){
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE)
            {
                throw  new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // 当前这一堆菜品是否可以删除，是否其中有的菜品存在于其他套餐中
        List<Long> setmealIds = setmealDishMapper.getSetmealDishIdsByDishId(ids);
        if(setmealIds != null && setmealIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 最后最终可以删除
        dishMapper.deleteById(ids);
        dishFlavorMapper.deleteByDishId(ids);

    }

    @Override
    public void updateById(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.updateById(dish);
        List<Long>dishIds = new ArrayList<>();
        dishIds.add(dishDTO.getId());

        dishFlavorMapper.deleteByDishId(dishIds);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){

            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dish.getId());
            });
        }
        dishFlavorMapper.insertBatch(flavors);

    }

    @Override
    public DishVO getByIdwithFlavor(Long id) {
       // 根据菜品id去查询id
        Dish dish =  dishMapper.getById(id);

        // 根据菜品id查询口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    @Override
    public List<Dish> getAllByCategoryId(Long categoryId) {
        return dishMapper.getAllByCategoryId(categoryId);
    }

    @Override
    public void updateStatusById(Integer status, Long id) {
       // 构建一个新的dish
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();

        dishMapper.updateById(dish);
    }

    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
