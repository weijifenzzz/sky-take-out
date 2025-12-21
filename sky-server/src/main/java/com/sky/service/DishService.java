package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 菜品分页查询
     */
    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    DishVO getById(Long id);

    void saveWithFlavor(DishDTO dishDTO);  // 在Java接口中，所有方法默认都是 public 访问级别的

    void delete(List<Long> ids);

    void update(DishDTO dishDTO);

    List<Dish> list(Long categoryId);

    void startOrStop(Integer status, Long id);
}
