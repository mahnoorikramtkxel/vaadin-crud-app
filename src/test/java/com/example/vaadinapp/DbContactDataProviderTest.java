package com.example.vaadinapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DbContactDataProviderTest {

    @Mock
    private ContactDao contactDao;
    private DbContactDataProvider dataProvider;

    private Contact contact1;
    private Contact contact2;

    @BeforeEach
    void setUp() {
        contact1 = new Contact("John", "Doe", "Street 1", "CityA", "CountryX", "123", "john@example.com", "1");
        contact2 = new Contact("Jane", "Smith", "Street 2", "CityB", "CountryY", "456", "jane@example.com", "2");
        when(contactDao.getAllContacts()).thenReturn(List.of(contact1, contact2));
        dataProvider = new DbContactDataProvider(contactDao);
    }

    @Test
    void testFindById() {
        Optional<Contact> result = dataProvider.findById("1");
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
    }

    @Test
    void testFindByPhone() {
        Optional<Contact> result = dataProvider.findByPhone("456");
        assertTrue(result.isPresent());
        assertEquals("Jane", result.get().getFirstName());
    }

    @Test
    void testSaveContactAndRefreshCache() {
        Contact contact3 = new Contact("Ali", "Khan", "Street 3", "CityC", "CountryZ", "789", "ali@example.com", "3");

        doNothing().when(contactDao).save(contact3);
        when(contactDao.getAllContacts()).thenReturn(List.of(contact1, contact2, contact3));

        dataProvider.save(contact3);

        Optional<Contact> result = dataProvider.findById("3");
        assertTrue(result.isPresent());
        assertEquals("Ali", result.get().getFirstName());

        verify(contactDao).save(contact3);
        verify(contactDao, times(2)).getAllContacts(); // once at setup, once after save
    }

    @Test
    void testDeleteContactAndRefreshCache() {
        doNothing().when(contactDao).remove("2");
        when(contactDao.getAllContacts()).thenReturn(List.of(contact1)); // simulate contact2 removed

        dataProvider.delete(contact2);

        Optional<Contact> result = dataProvider.findById("2");
        assertFalse(result.isPresent());

        verify(contactDao).remove("2");
        verify(contactDao, times(2)).getAllContacts();
    }

    @Test
    void testFindById_NotFound() {
        Optional<Contact> result = dataProvider.findById("nonexistent");
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByPhone_NotFound() {
        Optional<Contact> result = dataProvider.findByPhone("999");
        assertFalse(result.isPresent());
    }
}
