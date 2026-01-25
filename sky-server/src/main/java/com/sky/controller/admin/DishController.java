package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.impl.DishServiceImpl;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {
    @Autowired
    private DishServiceImpl dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 菜品分页查询
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){  // 前端传过来的不是json格式数据，而是普通参数
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询菜品,用于页面回显
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){   //路径变量。通过@PathVariable注解获取URL中的ID参数
        log.info("根据id查询菜品：{}", id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }


    /**
     * 新增菜品
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){   // 请求体。前端传过来的json格式数据要用@RequestBody注解接收
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);

        cleanCache("dish_"+dishDTO.getCategoryId());

        return Result.success();
    }

    /**
     * 批量删除菜品
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result delete(@RequestParam List<Long> ids){ // 请求参数。多个参数用@RequestParam注解接收
        log.info("批量删除菜品：{}", ids);
        dishService.delete(ids);
        //清理缓存数据
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 修改菜品
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}", dishDTO);
        dishService.update(dishDTO);

        //清理缓存数据,涉及分类表，较为复杂，因此全删了
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(@RequestParam Long categoryId){
        log.info("根据分类id查询菜品：{}", categoryId);
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 菜品起售、停售
     */
    @PostMapping("status/{status}")
    @ApiOperation("菜品起售、停售")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("菜品起售、停售：{},{}", status, id);
        dishService.startOrStop(status, id);

        cleanCache("dish_*");

        return Result.success();
    }


    /**
     * 清理缓存数据
     */
    private void cleanCache(String pattern){
        redisTemplate.delete(
                redisTemplate.keys(pattern)
        );
    }





}
