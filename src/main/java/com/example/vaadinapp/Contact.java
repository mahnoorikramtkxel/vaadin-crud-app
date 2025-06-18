package com.example.vaadinapp;

import java.util.Objects;
import java.util.UUID;

public class Contact {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id = UUID.randomUUID().toString();
    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String country;
    private String phone;
    private String email;


    public Contact(String firstName, String lastName, String street, String city, String country, String phone, String email, String id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.phone = phone;
        this.email = email;
        this.city = city;
        this.country = country;
        this.id = id;
    }
    public Contact(Contact other) {
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.email = other.email;
        this.phone = other.phone;
        this.street = other.street;
        this.city = other.city;
        this.country = other.country;
        this.id = other.id;
    }

    public Contact() {}

    // Getters and Setters

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
