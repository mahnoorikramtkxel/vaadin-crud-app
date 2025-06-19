# Vaadin Contact CRUD App

This is a simple Vaadin-based CRUD application for managing contacts.  
It allows switching between two data sources at runtime:

- **In-Memory Map** 
- **MySQL Database** 

---

## Features

- Create, edit, delete contacts
- Unique phone number validation
- Edit conflict prevention across users
- Switch data source at runtime via a radio button
- Data-source-specific locking (same contact can be edited independently in DB and Map views)

---

## Technologies Used

- Java
- Vaadin Flow
- MySQL
- Maven
- Jetty (run with `mvn jetty:run`)


