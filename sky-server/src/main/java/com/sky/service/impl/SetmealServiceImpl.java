package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.*;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    DishMapper dishMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;

    @Override
    public void addSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.addSetmeal(setmeal);

        // 获取生成的套餐id
        Long setmealId = setmeal.getId();

//        // 还要往Setmeal——dish表中插入
//        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
//        for (SetmealDish setmealDish : setmealDishes) {
//            setmealDish.setSetmealId(setmealId);
//        }
//
//        //往setmeal_Dish表中插入
//        setmealDishMapper.insertBatch(setmealDishes);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });

        //保存套餐和菜品的关联关系
        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        Page<Setmeal> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void deleteSetmealByIds(List<Long> ids) {
        List<Integer> statuses = setmealMapper.getAllStatus(ids);
        for(Integer status : statuses) {
            if(Objects.equals(status, StatusConstant.ENABLE)) {
                // 启售中的套餐不能删除
                throw  new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
            // 把 setmeal_id 表中的相关数据删除 先删除外键
            setmealDishMapper.deleteSetmeal_DishBySetmealIds(ids);

            setmealMapper.deleteSetmealsByIds(ids);
    }

    @Override
    public SetmealVO getSetmealById(Long id) {
        Setmeal setmeal =  setmealMapper.getSetmealById(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;

    }

    @Override
    @Transactional
    public void updateSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        // 首先更新
        setmealMapper.updateById(setmeal);
        List<Long> setmealDishesIds = new ArrayList<>();
        setmealDishesIds.add(setmealDTO.getId());

        // 然后删除所有原有的套餐
        setmealDishMapper.deleteSetmeal_DishBySetmealIds(setmealDishesIds);

//        获得 新的数据
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        // 给这些
        if(setmealDishes != null && setmealDishes.size() > 0){
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmeal.getId());
            });
        }
        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public void updateSetmealStatus(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();

        setmealDishMapper.updateSetmealById(setmeal);
    }

    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        return setmealMapper.list(setmeal);
    }

    @Override
    public List<DishItemVO> getDishItemBySetmealId(Long id) {
        // 获取所有Setmeal_id 为 id 的项
        List<SetmealDish> bySetmealId = setmealDishMapper.getBySetmealId(id);
        List<DishItemVO> dishItemVOList = new ArrayList<>();
//        将其转化为 DishItemVO
        for(SetmealDish setmealDish : bySetmealId) {
            DishItemVO dishItemVO = new DishItemVO();
            BeanUtils.copyProperties(setmealDish, dishItemVO);
            dishItemVOList.add(dishItemVO);
        }
        return dishItemVOList;

    }


}
