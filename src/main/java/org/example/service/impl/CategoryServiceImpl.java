package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.CustomException;
import org.example.entity.Category;
import org.example.entity.Dish;
import org.example.entity.Setmeal;
import org.example.mapper.CategoryMapper;
import org.example.service.CategoryService;
import org.example.service.DishService;
import org.example.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<Dish>();
        dishQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishQueryWrapper);

        if (count > 0){
            throw new CustomException("分类下关联菜品不能删除");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if (count1 > 0){
            throw new CustomException("分类下关联套餐不能删除");
        }

        removeById(id);
    }
}
