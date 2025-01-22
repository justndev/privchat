package com.ndev.privchat.privchat.service;

import com.ndev.privchat.privchat.entities.MessageRecord;
import com.ndev.privchat.privchat.entities.RegistrationRecord;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class SQLiteService {

    private static final String DATABASE_URL = "jdbc:sqlite:service.db";

    public SQLiteService() {
        createDatabaseAndTables();
    }

    // Create database and tables if they don't exist
    private void createDatabaseAndTables() {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {

            // Create messages table
            String createMessagesTable = "CREATE TABLE IF NOT EXISTS messages (\n"
                    + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                    + "    time TEXT NOT NULL\n"
                    + ");";
            stmt.execute(createMessagesTable);

            // Create registrations table
            String createRegistrationsTable = "CREATE TABLE IF NOT EXISTS registrations (\n"
                    + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                    + "    time TEXT NOT NULL\n"
                    + ");";
            stmt.execute(createRegistrationsTable);

            // Create records table
            String createRecordsTable = "CREATE TABLE IF NOT EXISTS records (\n"
                    + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                    + "    all_registrations INTEGER DEFAULT 0,\n"
                    + "    all_sent_messages INTEGER DEFAULT 0\n"
                    + ");";
            stmt.execute(createRecordsTable);

            // Ensure a single record exists in the records table
            String insertInitialRecord = "INSERT INTO records (all_registrations, all_sent_messages)\n"
                    + "SELECT 0, 0 WHERE NOT EXISTS (SELECT 1 FROM records);";
            stmt.execute(insertInitialRecord);

        } catch (SQLException e) {
            System.err.println("Error creating database or tables: " + e.getMessage());
        }
    }

    // Insert a new time value into the messages table
    public void addMessageTime(String time) {
        String insertMessage = "INSERT INTO messages (time) VALUES (?);";
        String updateMessagesCount = "UPDATE records SET all_sent_messages = all_sent_messages + 1;";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertMessage);
             Statement stmt = conn.createStatement()) {

            pstmt.setString(1, time);
            pstmt.executeUpdate();
            stmt.execute(updateMessagesCount);

        } catch (SQLException e) {
            System.err.println("Error adding message time: " + e.getMessage());
        }
    }

    // Insert a new time value into the registrations table
    public void addRegistrationTime(String time) {
        String insertRegistration = "INSERT INTO registrations (time) VALUES (?);";
        String updateRegistrationsCount = "UPDATE records SET all_registrations = all_registrations + 1;";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertRegistration);
             Statement stmt = conn.createStatement()) {

            pstmt.setString(1, time);
            pstmt.executeUpdate();
            stmt.execute(updateRegistrationsCount);

        } catch (SQLException e) {
            System.err.println("Error adding registration time: " + e.getMessage());
        }
    }

    // Retrieve all messages as a list of objects
    public List<MessageRecord> getMessages() {
        String selectMessages = "SELECT * FROM messages;";
        List<MessageRecord> messages = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectMessages)) {

            while (rs.next()) {
                messages.add(new MessageRecord(rs.getInt("id"), rs.getString("time")));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving messages: " + e.getMessage());
        }

        return messages;
    }

    // Retrieve all registrations as a list of objects
    public List<RegistrationRecord> getRegistrations() {
        String selectRegistrations = "SELECT * FROM registrations;";
        List<RegistrationRecord> registrations = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectRegistrations)) {

            while (rs.next()) {
                registrations.add(new RegistrationRecord(rs.getInt("id"), rs.getString("time")));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving registrations: " + e.getMessage());
        }

        return registrations;
    }

    // Retrieve all records from messages and registrations tables
    public void getAll() {
        String selectMessages = "SELECT * FROM messages;";
        String selectRegistrations = "SELECT * FROM registrations;";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {

            System.out.println("Messages:");
            try (ResultSet rs = stmt.executeQuery(selectMessages)) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + ", Time: " + rs.getString("time"));
                }
            }

            System.out.println("\nRegistrations:");
            try (ResultSet rs = stmt.executeQuery(selectRegistrations)) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + ", Time: " + rs.getString("time"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SQLiteService service = new SQLiteService();

        // Example usage
        service.addMessageTime("2025-01-13 10:00:00");
        service.addRegistrationTime("2025-01-13 11:00:00");

        service.getAll();

        // Fetch messages and registrations
        List<MessageRecord> messages = service.getMessages();
        List<RegistrationRecord> registrations = service.getRegistrations();

        System.out.println("\nMessages List:");
        for (MessageRecord message : messages) {
            System.out.println(message);
        }

        System.out.println("\nRegistrations List:");
        for (RegistrationRecord registration : registrations) {
            System.out.println(registration);
        }
    }
}




