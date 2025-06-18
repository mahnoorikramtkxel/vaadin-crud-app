package com.example.vaadinapp;

import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;

import java.util.Optional;

abstract class ContactDataProvider extends AbstractBackEndDataProvider<Contact, CrudFilter> {
    abstract void save(Contact item);

    abstract Optional<Contact> findById(String id);

    abstract Optional<Contact> findByPhone(String phone);

    abstract void delete(Contact item);
}
