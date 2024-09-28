package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private WorkspaceService workspaceService;
//    @Autowired
//    private ErrorMvcAutoConfiguration.WhitelabelErrorViewConfiguration whitelabelErrorViewConfiguration;

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 当前集合用于存放从begin到end范围内的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);

        while (!begin.equals(end)) {
            // 日期计算，计算指定日期的后一天对应的日期
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 转成字符串
        String str = StringUtils.join(dateList, ",");

        //存放每天的营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            // 查询date这一天已完成的金额
            // 借用已经创建好的工具类，获取当前日期0点0分0秒
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            // 截至当前日期的23.59.59秒
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = (turnover == null ? 0 : turnover);
            turnoverList.add(turnover);

        }

        String turnoverStr = StringUtils.join(turnoverList, ",");
        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                .dateList(str)
                .build();

        turnoverReportVO.setTurnoverList(turnoverStr);
        return turnoverReportVO;
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
        //        private String dateList;

        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        String dateListstr = StringUtils.join(dateList, ",");

        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        // 记录截至目前日期一共有多少个用户
        int totalUserNum = 0;
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
//            //用户总量，以逗号分隔，例如：200,210,220
//            private String totalUserList;

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);

            Integer currentUserNum = userMapper.sumCurrentDate(map);

            currentUserNum = (currentUserNum == null ? 0 : currentUserNum);
            newUserList.add(currentUserNum);
            totalUserNum += currentUserNum;
            totalUserList.add(totalUserNum);

        }

        String newUserStr = StringUtils.join(newUserList, ",");
        String totalStr = StringUtils.join(totalUserList, ",");

        UserReportVO userReportVO = UserReportVO.builder()
                .dateList(dateListstr)
                .newUserList(newUserStr)
                .totalUserList(totalStr)
                .build();


        return userReportVO;
    }

    @Override
    @Transactional
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        String dateListstr = StringUtils.join(dateList, ",");

        // 截至当前日期的订单数量
        List<Integer> orderCountList = new ArrayList<>();
        // 截至当前日期的有效的订单数量
        List<Integer> validOrderList = new ArrayList<>();

        // 总的订单量
        int totalOrderNum = 0;
        // 有效订单数量
        int validOrderNum = 0;

        for (LocalDate localDate : dateList) {

            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);

            //  求当天的订单数量
            Integer currentOrderNum = orderMapper.getCurrentOrderNum(map);
            map.put("status", Orders.COMPLETED);

            // 求当天的有效的订单数量
            Integer currentValidOrderNum = orderMapper.getCurrentOrderNum(map);
            currentValidOrderNum = (currentValidOrderNum == null ? 0 : currentValidOrderNum);

            // 加入每一天的有效订单
            validOrderList.add(currentValidOrderNum);
            // 总的完成单数量
            validOrderNum += currentValidOrderNum;
            currentOrderNum = (currentOrderNum == null ? 0 : currentOrderNum);
            // 订单总数
            totalOrderNum += currentOrderNum;
            orderCountList.add(currentOrderNum);

        }

        String orderCountStr = StringUtils.join(orderCountList, ",");
        String validOrderCountStr = StringUtils.join(validOrderList, ",");
        double orderCompletionRate = (double) validOrderNum / totalOrderNum;

        OrderReportVO orderReportVO = OrderReportVO.builder()
                .dateList(dateListstr)
                .orderCountList(orderCountStr)
                .validOrderCountList(validOrderCountStr)
                .orderCompletionRate(orderCompletionRate)
                .totalOrderCount(totalOrderNum)
                .validOrderCount(validOrderNum).build();

        //有效订单数
        return orderReportVO;

    }

    @Override
    public SalesTop10ReportVO getsalesTop10(LocalDate begin, LocalDate end) {


        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop(beginTime, endTime);

        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameListStr = StringUtils.join(names, ",");

        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberListStr = StringUtils.join(numbers, ",");

        SalesTop10ReportVO salesTop10ReportVO = SalesTop10ReportVO.builder()
                .nameList(nameListStr)
                .numberList(numberListStr)
                .build();


        return salesTop10ReportVO;
    }

    @Override
    public void exportBusinessData(HttpServletResponse response) {
        // 1. 查询数据库，获取文件数据-- 查询最近30天的
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        // 查询概览数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(dateBegin, dateEnd);

        // 2. 通过POI将数据导入到Excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");


        // 基于模板文件创建一个新的Excel文件
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);
            // 填充数据
            XSSFSheet sheet = excel.getSheet("Sheet1");

            // 填充数据 - 时间
            sheet.getRow(1).getCell(1).setCellValue("时间：" + dateBegin + "至" + dateEnd);

            // 获得第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            // 获得第5行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                // 查询某一天的营业数据
                BusinessDataVO businessData = workspaceService.getBusinessData(date, date);

                // 获得某一行
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());

            }


            // 3. 通过输出流将Excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭资源
            out.close();
            excel.close();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
