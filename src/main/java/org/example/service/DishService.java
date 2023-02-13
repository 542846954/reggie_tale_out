package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dto.DishDto;
import org.example.entity.Dish;

public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);

    DishDto getDishWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);
}
