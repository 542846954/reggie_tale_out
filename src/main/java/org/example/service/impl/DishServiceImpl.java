package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.dto.DishDto;
import org.example.entity.Dish;
import org.example.entity.DishFlavor;
import org.example.mapper.DishMapper;
import org.example.service.DishFlavorService;
import org.example.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService flavorService;
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        /*保存菜品的基本信息到菜品表*/
        this.save(dishDto);
        Long dishId = dishDto.getId();
        /*保存菜品口味数据*/
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(item -> item.setDishId(dishId));
        flavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getDishWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> dishFlavors = flavorService.list(queryWrapper);

        dishDto.setFlavors(dishFlavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        /*更新dish表基本信息*/
        boolean updateDish = this.updateById(dishDto);
        /*清理菜品对应口味信息*/
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        boolean removeFlavor = flavorService.remove(queryWrapper);
        /*添加菜品对应口味信息*/
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(item -> item.setDishId(dishDto.getId()));
        boolean saveFlavors = flavorService.saveBatch(flavors);
    }
}
