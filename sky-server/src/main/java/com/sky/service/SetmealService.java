package com.sky.service;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {



    List<Setmeal> getByIdList(Long id);

    List<DishItemVO> getDishItemById(Long id);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void add(SetmealDTO setmealDTO);

    void update(SetmealDTO setmealDTO);

    SetmealVO getById(Long id);

    void delete(List<Long> ids);

    void startOrStop(Integer status, Long id);
}
