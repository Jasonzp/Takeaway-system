package com.sky.controller.user;


import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "C端地址接口")
@Slf4j
public class AddressBookController {


    @Autowired
    private AddressBookService addressBookService;


    @PostMapping
    @ApiOperation("新增地址")
    public Result add(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookService.add(addressBook);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("查询当前登录用户所有地址")
    public Result<List<AddressBook>> list() {
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> addressBookList= addressBookService.list(addressBook);

        return Result.success(addressBookList);

    }

    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> defaultAddressBook() {
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(1);

      List<AddressBook> addressBookDefault = addressBookService.list(addressBook);
       if(addressBookDefault != null && addressBookDefault.size() > 0) {
           return Result.success(addressBookDefault.get(0));
       }
       return Result.error("没有找到默认地址");
    }

    @DeleteMapping
    @ApiOperation("删除地址")
    public Result delete(Long id) {
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setId(id);
        addressBookService.deleteById(addressBook);
        return Result.success();
    }
    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> get(@PathVariable Long id) {
        AddressBook addressBook = new AddressBook();
        addressBook.setId(id);
        addressBook.setUserId(BaseContext.getCurrentId());

        List<AddressBook> addressBookList = addressBookService.list(addressBook);
        if(addressBookList != null && addressBookList.size() > 0) {
            return Result.success(addressBookList.get(0));
        }
        return Result.error("没有对应id的地址");

    }

    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result updateDefault(@RequestBody AddressBook addressBook) {
        AddressBook addressBook1 = new AddressBook();
        addressBook1.setUserId(BaseContext.getCurrentId());
        addressBook1.setIsDefault(1);


        // 查出之前是否有默认的地址
        List<AddressBook> addressBookList = addressBookService.list(addressBook1);
        if(addressBookList != null && addressBookList.size() > 0) {
            AddressBook addressBookBeforeDefault = addressBookList.get(0);
            addressBookService.updateIs_Default(addressBookBeforeDefault);
        }
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookService.updateIs_Default(addressBook);

        return Result.success();

    }
    @PutMapping
    @ApiOperation("根据id修改地址")
    public Result update(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.update(addressBook);
        return Result.success();
    }




}
