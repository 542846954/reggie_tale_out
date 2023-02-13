package org.example.dto;

import lombok.Data;
import org.example.entity.Setmeal;
import org.example.entity.SetmealDish;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
