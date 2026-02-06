package com.sky.controller.admin;


import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Api(tags = "订单管理接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageResult pageResult = orderService.conditionSearch(ordersPageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 查看订单详情
     */
    @GetMapping("details/{id}")
    @ApiOperation("查看订单详情")
    public Result<OrderVO> seeDetails(@PathVariable Long id){
        OrderVO orderVO = orderService.seeDetails(id);

        return Result.success(orderVO);
    }

    /**
     * 取消订单
     */
    @PutMapping("cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        orderService.cancel(ordersCancelDTO);

        return Result.success();
    }


    /**
     * 统计各个状态的订单数量
     */
    @GetMapping("statistics")
    @ApiOperation("统计各个状态的订单数量")
    public Result statistics(){
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 完成订单
     */
    @PutMapping("complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable Long id){
        orderService.complete(id);
        return Result.success();
    }

    /**
     * 拒单
     */
    @PutMapping("reject")
    @ApiOperation("拒单")
    public Result reject(@RequestBody OrdersRejectionDTO ordersRejectionDTO){

        orderService.reject(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 接单
     */
    @PutMapping("confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 派送订单
     */
    @PutMapping("delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable Long id){
        orderService.delivery(id);
        return Result.success();
    }
}
