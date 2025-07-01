# Vaadin Contact CRUD App

This is a simple Vaadin-based CRUD application for managing contacts.  
It allows switching between two data sources at runtime:

- **In-Memory Map** 
- **MySQL Database** 

---

## Features

- Create, edit, delete contacts
- Unique phone number validation
- Edit conflict prevention across users/tabs
- Switch data source at runtime via a radio button
- Data-source-specific locking (same contact can be edited independently in DB and Map views)

---

## Technologies Used

- Java 17
- Vaadin Flow - Vaadin CRUD
- MySQL
- Maven
- Jetty (run with `mvn jetty:run`)

---

## Screenshots

<img width="1792" alt="image" src="https://github.com/user-attachments/assets/fb41659a-ce01-49d1-bee2-f73268439b84" />
---
<img width="1792" alt="image" src="https://github.com/user-attachments/assets/15442259-3a3c-4a4b-a57b-c83dae33ba4d" />
---
<img width="282" alt="image" src="https://github.com/user-attachments/assets/b1e7cb00-e17d-4f42-9f3a-2be29cd4afc6" />
