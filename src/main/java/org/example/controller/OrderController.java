package org.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseContext;
import org.example.common.R;
import org.example.dto.OrdersDto;
import org.example.entity.OrderDetail;
import org.example.entity.Orders;
import org.example.service.OrderDetailService;
import org.example.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize) {
        log.info("入参page:{},pageSize:{},name:{}", page, pageSize);
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        Page<Orders> result = orderService.page(pageInfo, queryWrapper);
        List<Orders> records = result.getRecords();
        List<OrdersDto> orderDtoList = records.stream().map(item -> {
            OrdersDto dto = new OrdersDto();
            BeanUtils.copyProperties(item, dto);
            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OrderDetail::getOrderId, item.getId());
            List<OrderDetail> list = orderDetailService.list(lambdaQueryWrapper);
            dto.setOrderDetails(list);
            return dto;
        }).collect(Collectors.toList());
        Page<OrdersDto> ordersDtoPage = new Page<>();
        BeanUtils.copyProperties(result, ordersDtoPage, "records");
        ordersDtoPage.setRecords(orderDtoList);
        return R.success(ordersDtoPage);
    }
}
