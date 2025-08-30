//package me.besser;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//public class ACAPIDatabase {
//    private final Connection connection;
//
//    /**
//     * Connects to the database and creates the file and tables if they do not exist already.
//     */
//    public ACAPIDatabase(String path) throws SQLException {
//        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
//
//        try (Statement statement = connection.createStatement()) {
//            statement.execute("""
//            CREATE TABLE IF NOT EXISTS players (
//            uuid TEXT PRIMARY KEY,
//            bio TEXT NOT NULL DEFAULT '')
//            """);
//        }
//    }
//
//    public void closeConnection() throws SQLException {
//        if (connection != null && !connection.isClosed()) {
//            connection.close();
//        }
//    }
//}
