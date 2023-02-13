package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.entity.Setmeal;
import org.example.entity.SetmealDish;

@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {
}
