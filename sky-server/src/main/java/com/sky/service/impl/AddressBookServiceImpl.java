package com.sky.service.impl;


import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Override
    public void add(AddressBook addressBook) {

        addressBookMapper.add(addressBook);
    }

    @Override
    public List<AddressBook> list(AddressBook addressBook) {
        return addressBookMapper.list(addressBook);
    }

    @Override
    public AddressBook listDefault(AddressBook addressBook) {
        addressBook.setIsDefault(1);
        List<AddressBook> list = addressBookMapper.list(addressBook);
        return list.get(0);
    }

    @Override
    public void deleteById(AddressBook addressBook) {
        addressBookMapper.deleteById(addressBook);
    }

    @Override
    public void updateIs_Default(AddressBook addressBookBeforeDefault) {
        if (addressBookBeforeDefault.getIsDefault() == 1)
        {
            // 之前是默认值
            addressBookBeforeDefault.setIsDefault(0);
        }else {
            addressBookBeforeDefault.setIsDefault(1);
        }
        addressBookMapper.update(addressBookBeforeDefault);
    }

    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }
}
