package com.sky.service.impl;


import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;


    @Override
    public void save(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.save(addressBook);
    }

    @Override
    public List<AddressBook> list() {
        Long userId = BaseContext.getCurrentId();
        return addressBookMapper.list(userId);
    }

    @Override
    public AddressBook getDefault() {
        return addressBookMapper.getDefault(BaseContext.getCurrentId());
    }

    @Override
    public void setDefault(AddressBook addressBook) {
        //检查是否存在已有默认地址
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.setDefaultByUserId(addressBook);

        addressBook = addressBook.builder()
                .id(addressBook.getId())
                .isDefault(1)
                .build();
        addressBookMapper.update(addressBook);


    }

    @Override
    public AddressBook getById(Long id) {
        Long userId = BaseContext.getCurrentId();
        return addressBookMapper.getById(id, userId);
    }

    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    @Override
    public void delete(Long id) {
        addressBookMapper.delete(id);
    }


}
