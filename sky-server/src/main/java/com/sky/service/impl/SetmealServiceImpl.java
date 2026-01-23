package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;


    @Override
    public List<Setmeal> getByIdList(Long id) {
        List<Setmeal> list = setmealMapper.getByIdList(id);
        return  list;
    }

    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemById(id);

    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper .startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());

    }

    @Override
    public void add(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        setmeal.setStatus(StatusConstant.ENABLE);

        setmealMapper.insert(setmeal);
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
            setmealMapper.insertSetmealDish(setmealDish);
        }



    }

    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        //修改套餐
        setmealMapper.update(setmeal);

        //修改套餐菜品关系
        //删除后添加
        setmealMapper.deleteSetmealDishBySetmealId(setmeal.getId());
        for (SetmealDish setmealDish : setmealDishes){
            setmealDish.setSetmealId(setmeal.getId());
            setmealMapper.insertSetmealDish(setmealDish);

        }

    }

    @Override
    @Transactional
    public SetmealVO getById(Long id) {
        SetmealVO setmealVO = setmealMapper.getById(id);
        setmealVO.setSetmealDishes(setmealMapper.getSetmealDishBySetmealId(id));
        return setmealVO;
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        //删除套餐和套餐菜品关系
        for (Long id : ids){
            setmealMapper.delete(id);
            setmealMapper.deleteSetmealDishBySetmealId(id);
        }

    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

}
