//package me.besser;
//
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//
//import java.io.File;
//import java.sql.*;
//
//import static me.besser.BesserLogger.*;
//
//public class BiographyCommand implements CommandExecutor {
//
//    private final String dbUrl = "jdbc:sqlite:" + new File("plugins/AtlasCivs-API/acapi.db").getAbsolutePath();
//
//
//    // TODO: create general command handler class so we dont have to do everything here in case of
//    // future expansion
//
//    @Override
//    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//        if (!(sender instanceof Player player)) {
//            sender.sendMessage("§cOnly players can use this command.");
//            return true;
//        }
//
//        // No args == show current bio
//        if (args.length == 0) {
//            String bio = getBiography(player.getUniqueId().toString());
//            if (bio == null || bio.isEmpty()) {
//                player.sendMessage("§eYou don’t have a biography set. Use §a/atlas bio <text>§e to set one!");
//            } else {
//                player.sendMessage("§aYour biography: §f" + bio);
//            }
//            return true;
//        }
//
//        // Clear == clear bio
//        if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
//            clearBiography(player.getUniqueId().toString());
//            player.sendMessage("§aYour biography has been cleared!");
//            return true;
//        }
//
//        // Otherwise, join args into the biography string
//        String biography = String.join(" ", args);
//
//        if (biography.length() > 100) {
//            player.sendMessage("§cYour biography is too long! (Max 100 characters)");
//            return true;
//        }
//
//        saveBiography(player.getUniqueId().toString(), biography);
//        player.sendMessage("§aYour biography has been updated!");
//        return true;
//    }
//
//    private void saveBiography(String uuid, String bio) {
//        try (Connection conn = DriverManager.getConnection(dbUrl)) {
//            String sql = "INSERT INTO bio (uuid, bio) VALUES (?, ?) " +
//                    "ON CONFLICT(uuid) DO UPDATE SET bio = excluded.bio;";
//            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//                pstmt.setString(1, uuid);
//                pstmt.setString(2, bio);
//                pstmt.executeUpdate();
//            }
//        } catch (SQLException e) {
//            log(WARNING, e.getMessage());
//        }
//    }
//
//    private String getBiography(String uuid) {
//        try (Connection conn = DriverManager.getConnection(dbUrl)) {
//            String sql = "SELECT bio FROM bio WHERE uuid = ?;";
//            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//                pstmt.setString(1, uuid);
//                ResultSet rs = pstmt.executeQuery();
//                if (rs.next()) {
//                    return rs.getString("bio");
//                }
//            }
//        } catch (SQLException e) {
//            log(WARNING, e.getMessage());
//        }
//        return null;
//    }
//
//    private void clearBiography(String uuid) {
//        try (Connection conn = DriverManager.getConnection(dbUrl)) {
//            String sql = "DELETE FROM bio WHERE uuid = ?;";
//            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//                pstmt.setString(1, uuid);
//                pstmt.executeUpdate();
//            }
//        } catch (SQLException e) {
//            log(WARNING, e.getMessage());
//        }
//    }
//}