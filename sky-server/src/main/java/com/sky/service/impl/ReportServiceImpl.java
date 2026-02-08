package com.sky.service.impl;


import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Dish;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private WorkSpaceService workSpaceService;
    @Autowired
    private ReportService reportService;

    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {

        // 计算两个日期之间的天数差
        long daysBetween = ChronoUnit.DAYS.between(begin, end);

        // 生成日期列表（包含首尾）
        List<LocalDate> dateList = new ArrayList<>();
        for (long i = 0; i <= daysBetween; i++) {
            dateList.add(begin.plusDays(i));  // begin.plusDays，日期加 i 天
        }

        List<Double> turnoverList = new ArrayList<>();

        for (LocalDate date : dateList) {
            //查询date日期对于的营业额数据,营业额为状态是订单已完成的数据合计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);  // date的开始时间0点0分0秒
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);


            turnoverList.add(turnover == null ? 0.0 : turnover);

        }

        //返回结果
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }


    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //存放begin到end每天对应的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> newUserList = new ArrayList<>(); // 新增用户列表
        List<Integer> totalUserList = new ArrayList<>(); // 总用户列表

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end", endTime);
            Integer totalUser = userMapper.countUserByDate(map);  //总用户数量
            totalUserList.add(totalUser);
            map.put("begin", beginTime);
            Integer newUser = userMapper.countUserByDate(map); //新增用户数量
            newUserList.add(newUser);

        }



        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        //天数
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //订单总数和有效订单数
        List<Integer> allOrderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer allOrderCount = getOrderCount(beginTime, endTime, null);
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            allOrderCountList.add(allOrderCount);
            validOrderCountList.add(validOrderCount);
        }

        //订单总数
        Integer totalOrderCount = allOrderCountList.stream().reduce(Integer::sum).orElse(0);
        //有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).orElse(0);
        //完成率
        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(allOrderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
//        //售出的菜品列表
//        List<String> nameList = new ArrayList<>();
//        List<Integer> numberList = new ArrayList<>();

        Map map = new HashMap();
        map.put("begin", beginTime);
        map.put("end", endTime);
        map.put("status", Orders.COMPLETED);

        List<GoodsSalesDTO> listTop10 = orderDetailMapper.selectSalesTop10(map);

//        for (GoodsSalesDTO goodsSalesDTO : listTop10) {
//            nameList.add(goodsSalesDTO.getName());
//            numberList.add(goodsSalesDTO.getNumber());
//        }

        //高级写法
        List<String> names = listTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numbers = listTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());


        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(names, ","))
                .numberList(StringUtils.join(numbers, ","))
                .build();
    }

    /**
     * 导出Excel运营数据报表
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //1、查询数据库，获取营业数据--- 最近30天的运营数据
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        BusinessDataVO businessData = workSpaceService.getBusinessData(beginTime, endTime);

        //2、通过POI将数据写入Excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            //基于模板文件创建新的Excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);

            //填充数据--时间
            XSSFSheet sheet = excel.getSheet("Sheet1");
            sheet.getRow(1).getCell(1).setCellValue("时间：" +begin + "至" + end);
            //填充概览数据--营业额、订单数、有效订单数、平均订单金额
            sheet.getRow(3).getCell(2).setCellValue(businessData.getTurnover());
            sheet.getRow(3).getCell(4).setCellValue(businessData.getOrderCompletionRate());
            sheet.getRow(3).getCell(6).setCellValue(businessData.getNewUsers());
            sheet.getRow(4).getCell(2).setCellValue(businessData.getValidOrderCount());
            sheet.getRow(4).getCell(4).setCellValue(businessData.getUnitPrice());

            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                BusinessDataVO bd = workSpaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));

                sheet.getRow(7 + i).getCell(1).setCellValue(date.toString());
                sheet.getRow(7 + i).getCell(2).setCellValue(bd.getTurnover());
                sheet.getRow(7 + i).getCell(3).setCellValue(bd.getValidOrderCount());
                sheet.getRow(7 + i).getCell(4).setCellValue(bd.getOrderCompletionRate());
                sheet.getRow(7 + i).getCell(5).setCellValue(bd.getUnitPrice());
                sheet.getRow(7 + i).getCell(6).setCellValue(bd.getNewUsers());

            }

            //3、通过输出流将Excel文件下载到浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭资源
            out.close();
            excel.close();
        }catch(IOException e){
            e.printStackTrace();
        }






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


}
