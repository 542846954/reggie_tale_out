package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}
