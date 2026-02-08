package com.sky.service.impl;


import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.OrderService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private DishMapper dishMapper;



    @Override
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {

        //获取当日营业额
        Double turnover = getOrderAmount(begin, end, Orders.COMPLETED);
        turnover = turnover == null ? 0.0 : turnover;

        //获取当日有效订单数
        Integer validOrderCount = getOrderCount(begin, end, Orders.COMPLETED);

        //订单完成率
        Integer totalOrderCount = getOrderCount(begin, end, null);
        Double orderCompletionRate = 0.0;
        if (validOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        //平均客单价
        Double unitPrice = 0.0;
        if (validOrderCount != 0) {
            unitPrice = turnover / validOrderCount;
        }

        //新增用户数
        Integer newUsers = getNewUsers(begin, end);


        return BusinessDataVO.builder()
                .turnover( turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

    @Override
    public SetmealOverViewVO overviewSetmeals() {
        //已启售数量
        Integer sold = setmealMapper.getCountByStatus(StatusConstant.ENABLE);

        //已停售数量
        Integer discontinued = setmealMapper.getCountByStatus(StatusConstant.DISABLE);

        return SetmealOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }

    @Override
    public DishOverViewVO overviewDishes() {
        //已启售数量
        Integer sold = dishMapper.getCountByStatus(StatusConstant.ENABLE);

        //已停售数量
        Integer discontinued = dishMapper.getCountByStatus(StatusConstant.DISABLE);

        return DishOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }

    @Override
    public OrderOverViewVO overviewOrders() {
        //待接单数量
        Integer waitingOrders = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);

        //待派送数量
        Integer deliveredOrders = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

        //已完成数量
        Integer completedOrders = orderMapper.countStatus(Orders.COMPLETED);

        //已取消数量
        Integer cancelledOrders = orderMapper.countStatus(Orders.CANCELLED);

        //全部订单
        Integer allOrders = waitingOrders + deliveredOrders + completedOrders + cancelledOrders;

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }


    /**
     * 根据条件统计订单数量
     */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status){
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);

        return orderMapper.countByDateAndStatus(map);
    }

    private Double getOrderAmount(LocalDateTime begin, LocalDateTime end, Integer status){
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);

        return orderMapper.sumByMap(map);
    }

    private Integer getNewUsers(LocalDateTime begin, LocalDateTime end){
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);

        return userMapper.countUserByDate(map);
    }
}
