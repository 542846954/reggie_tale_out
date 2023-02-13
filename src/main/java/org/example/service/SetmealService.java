package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dto.SetmealDto;
import org.example.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /*新增套餐，同时保存套餐和菜品的关联关系*/
    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);
}
