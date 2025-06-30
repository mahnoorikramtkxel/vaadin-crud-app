package com.example.vaadinapp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContactDao {

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, email, phone, street, city, country FROM contact";

        try (Connection conn = DatabaseConn.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Contact contact = new Contact(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("street"),
                        rs.getString("city"),
                        rs.getString("country"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("id")
                );
                contacts.add(contact);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contacts;
    }

    public Contact findById(String id) {
        String sql = "SELECT id, first_name, last_name, email, phone, street, city, country FROM contact WHERE id = ?";

        try (Connection conn = DatabaseConn.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Contact(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("street"),
                            rs.getString("city"),
                            rs.getString("country"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getString("id")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Contact findByPhone(String phone) {
        String sql = "SELECT id, first_name, last_name, email, phone, street, city, country FROM contact WHERE phone = ?";

        try (Connection conn = DatabaseConn.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phone);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Contact(
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("street"),
                            rs.getString("city"),
                            rs.getString("country"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getString("id")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void save(Contact contact) {
        String updateSql = "UPDATE contact SET first_name = ?, last_name = ?, street = ?, city = ?, country = ?, phone = ?, email = ? WHERE id = ?";
        String insertSql = "INSERT INTO contact (id, first_name, last_name, street, city, country, phone, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConn.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            updateStmt.setString(1, contact.getFirstName());
            updateStmt.setString(2, contact.getLastName());
            updateStmt.setString(3, contact.getStreet());
            updateStmt.setString(4, contact.getCity());
            updateStmt.setString(5, contact.getCountry());
            updateStmt.setString(6, contact.getPhone());
            updateStmt.setString(7, contact.getEmail());
            updateStmt.setString(8, contact.getId());

            int rows = updateStmt.executeUpdate();

            if (rows == 0) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, contact.getId());
                    insertStmt.setString(2, contact.getFirstName());
                    insertStmt.setString(3, contact.getLastName());
                    insertStmt.setString(4, contact.getStreet());
                    insertStmt.setString(5, contact.getCity());
                    insertStmt.setString(6, contact.getCountry());
                    insertStmt.setString(7, contact.getPhone());
                    insertStmt.setString(8, contact.getEmail());
                    insertStmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(String id) {
        String sql = "DELETE FROM contact WHERE id = ?";

        try (Connection conn = DatabaseConn.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
