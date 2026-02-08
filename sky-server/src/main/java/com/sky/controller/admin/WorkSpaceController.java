package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminWorkSpaceController")
@RequestMapping("/admin/workspace")
@Api(tags = "工作台相关接口")
@Slf4j
public class WorkSpaceController {


    @Autowired
    private WorkSpaceService workSpaceService;

    /**
     * 查看今日运营数据
     */
    @GetMapping("businessData")
    @ApiOperation("查看今日运营数据")
    public Result<BusinessDataVO> getBusinessData(){
        log.info("查看今日运营数据");
        return Result.success(workSpaceService.getBusinessData());
    }

    /**
     * 查询套餐总览
     */
    @GetMapping("overviewSetmeals")
    @ApiOperation("查询套餐总览")
    public Result<SetmealOverViewVO> overviewSetmeals(){
        log.info("查询套餐总览");
        return Result.success(workSpaceService.overviewSetmeals());
    }

    /**
     * 菜品总览
     */
    @GetMapping("overviewDishes")
    @ApiOperation("菜品总览")
    public Result<DishOverViewVO> overviewDishes(){
        log.info("菜品总览");
        return Result.success(workSpaceService.overviewDishes());
    }

    /**
     * 订单管理
     */
    @GetMapping("overviewOrders")
    @ApiOperation("订单管理")
    public Result<OrderOverViewVO> overviewOrders(){
        log.info("订单管理");
        return Result.success(workSpaceService.overviewOrders());
    }

}
