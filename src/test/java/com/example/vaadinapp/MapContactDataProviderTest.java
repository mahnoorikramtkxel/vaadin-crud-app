package com.example.vaadinapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.helger.commons.mock.CommonsAssert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapContactDataProviderTest {

    private MapContactDataProvider dataProvider;
    private Contact contact;

    @BeforeEach
    void setUp() {
        dataProvider = new MapContactDataProvider();
        contact = new Contact("John", "Doe", "Street 2", "Fsd", "Pak", "090078601", "jdoe@gmail.com", "1");
        dataProvider.save(contact);
    }

    @Test
    void testSaveAndFindById() {
        Optional<Contact> result = dataProvider.findById("1");
        assertTrue(result.isPresent());
        assertEquals("1", result.get().getId());
    }

    @Test
    void testSaveFindByPhone() {
        Optional<Contact> result = dataProvider.findByPhone("090078601");
        assertTrue(result.isPresent());
        assertEquals("1", result.get().getId());
    }

    @Test
    void testDelete() {
        dataProvider.delete(contact);
        Optional<Contact> result = dataProvider.findById("1");
        assertFalse(result.isPresent());
    }
}