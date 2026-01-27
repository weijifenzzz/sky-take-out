package com.sky.mapper;


import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AddressBookMapper {


    @Insert("insert into address_book (user_id, consignee, phone, sex, province_code, province_name, city_code, city_name, district_code, district_name, detail, label, is_default) " +
            "values (#{userId}, #{consignee}, #{phone}, #{sex}, #{provinceCode}, #{provinceName},#{cityCode},#{cityName},#{districtCode},#{districtName},#{detail},#{label},#{isDefault})")
    void save(AddressBook addressBook);


    @Select("select * from address_book where user_id = #{userId}")
    List<AddressBook> list(Long userId);

    @Select("select * from address_book where user_id = #{userId} and is_default=1")
    AddressBook getDefault(Long currentId);

    @Select("select * from address_book where id = #{id} and user_id = #{userId}")
    AddressBook getById(Long id, Long userId);

    void update(AddressBook addressBook);

    @Select("delete from address_book where id = #{id}")
    void delete(Long id);


    @Select("update address_book set is_default=0 where user_id=#{userId}")
    void setDefaultByUserId(AddressBook addressBook);
}
