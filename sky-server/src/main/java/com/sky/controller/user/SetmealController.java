package com.sky.controller.user;


import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/user/setmeal")
@RestController("userSetmealController")
@Api(tags = "C端-套餐接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    public Result<List<Setmeal>> list(Long categoryId){
        List<Setmeal> list = setmealService.getByIdList(categoryId);
        return Result.success(list);
    }

    /**
     * 根据套餐id查询包含的菜品
     */
    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查询包含的菜品")
    public Result<List<DishItemVO>> dishList(@PathVariable Long id){
        List<DishItemVO> list = setmealService.getDishItemById(id);
        return Result.success(list);
    }



}
