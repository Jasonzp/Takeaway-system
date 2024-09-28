package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

   @Autowired
    ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    @Transactional
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
//        private Long dishId;
//        private Long setmealId;
//        private String dishFlavor;
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        // 传入userId
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

//        private Long id;
//        //名称
//        private String name;
//        //用户id
//        private Long userId;
//        //数量
//        private Integer number;
//        //金额
//        private BigDecimal amount;
//        //图片
//        private String image;
//        private LocalDateTime createTime;
        // 1.判断当前加入到购物车手中的商品是否已经存在
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        // 2.如果已经存在了，只需要将数量+1
        if(list != null && list.size() > 0) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber()+1);
            shoppingCartMapper.updateNumberById(cart);
        }else {
            // 3.如果不存在，需要插入一条购物车数据
            Long dishId = shoppingCartDTO.getDishId();
            if(dishId != null) {
                // 添加菜品
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());

            }else
            {
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getSetmealById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());

            }

            shoppingCartMapper.insert(shoppingCart);

        }





    }

    @Override
    public List<ShoppingCart> list() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list != null && list.size() > 0) {
            return list;
        }
     return Collections.emptyList();
    }

    @Override
    public void clean() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCartMapper.clean(shoppingCart);
    }

    @Override
    @Transactional
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list != null && list.size() > 0) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber()-1);
            if(cart.getNumber() <= 0) {
                // 如果删除后为 0，那么直接删除
                shoppingCartMapper.clean(shoppingCart);
            }else {
                // 删除后不为0
                shoppingCartMapper.updateNumberById(cart);
            }
        }

    }
}
