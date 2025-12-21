package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        //设置分页参数
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        //执行查询并返回结果
        Page<DishVO> p = dishMapper.pageQuery(dishPageQueryDTO);

        return new PageResult(p.getTotal(), p.getResult());
    }

    @Override
    public DishVO getById(Long id) {
        DishVO dishVO = dishMapper.getById(id);
        dishVO.setFlavors(dishMapper.getFlavorsByDishId(id));
        return dishVO;
    }

    @Override
    @Transactional  //通过@Transactional注解保证事务一致性
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 向菜品表中插入数据
        dishMapper.insert(dish);

        //获取生成的主键值（利用主键返回）
        Long dishId = dish.getId();

        // 向口味表中插入数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        //判断当前菜品是否能删除--是否存在起售中的菜品
        for (Long id : ids) {
            DishVO dishVO = dishMapper.getById(id);
            if(dishVO.getStatus()== StatusConstant.ENABLE){
                //菜品处于起售中，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);

            }
        }

        //判断当前菜品是否能删除--是否被套餐关联
        List<Long> setmealIds = setmealMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds!=null&&setmealIds.size()!=0){
            //当前菜品被套餐关联了
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }


        //删除菜品表中的数据
        dishMapper.delete(ids);

        //删除口味数据
        dishFlavorMapper.deleteFlavorByDishId(ids);
    }

    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        Long id = dishDTO.getId();
        List<Long> listId = new ArrayList<>();
        listId.add(id);

        // 删除原有的口味数据
        dishFlavorMapper.deleteFlavorByDishId(listId);

        // 重新插入新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    @Override
    public List<Dish> list(Long categoryId) {
        return dishMapper.list(categoryId);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder().
                status(status).
                id(id).
                build();
        dishMapper.update(dish);
    }
}
