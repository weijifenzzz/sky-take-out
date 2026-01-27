package com.sky.controller.user;


import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "C端-地址簿接口")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    @ApiOperation("添加地址")
    public Result add(@RequestBody AddressBook addressBook) {
        addressBookService.save(addressBook);

        return Result.success();
    }


    @GetMapping("/list")
    @ApiOperation("展示所有地址")
    public Result<List<AddressBook>> list() {
        List<AddressBook> list = addressBookService.list();
        return Result.success(list);
    }


    @GetMapping("default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> getDefault() {
        AddressBook addressBook = addressBookService.getDefault();
        return Result.success(addressBook);
    }

    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefault(@RequestBody AddressBook addressBook) {
        addressBookService.setDefault(addressBook);
        return Result.success();
    }


    @GetMapping("/{id}")
    @ApiOperation("查询指定地址")
    public Result<AddressBook> getById(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }


    @PutMapping
    @ApiOperation("修改地址")
    public Result update(@RequestBody AddressBook addressBook) {
        addressBookService.update(addressBook);
        return Result.success();
    }


    @DeleteMapping
    @ApiOperation("删除地址")
    public Result delete(Long id) {
        addressBookService.delete(id);
        return Result.success();
    }

}
