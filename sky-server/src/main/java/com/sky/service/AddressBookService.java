package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    void add(AddressBook addressBook);

    List<AddressBook> list(AddressBook addressBook);

    AddressBook listDefault(AddressBook addressBook);

    void deleteById(AddressBook addressBook);

    void updateIs_Default(AddressBook addressBookBeforeDefault);

    void update(AddressBook addressBook);
}
