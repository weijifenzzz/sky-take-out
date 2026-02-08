package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);


    /**
     * 根据菜品id查询对应的套餐id
     */
    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    @Select("select * from setmeal where category_id = #{id}")
    List<Setmeal> getByIdList(Long id);


    @Select("select sd.name, sd.copies, d.description, d.image from setmeal_dish sd left join dish d on sd.dish_id = d.id where sd.setmeal_id = #{id}")
    List<DishItemVO> getDishItemById(Long id);

    /**
     * 套餐分页查询
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);


    @AutoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);


    @Insert("insert into setmeal_dish (setmeal_id, dish_id, name, price, copies) " +
            "values (#{setmealId}, #{dishId}, #{name}, #{price}, #{copies})")
    void insertSetmealDish(SetmealDish setmealDish);


    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);



    SetmealVO getById(Long id);

    List<SetmealDish> getSetmealDishBySetmealId(Long id);

    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteSetmealDishBySetmealId(Long id);

    @Delete("delete from setmeal where id = #{id}")
    void delete(Long id);


    @Select("select count(id) from setmeal where status = #{status}")
    Integer getCountByStatus(Integer status);
}
