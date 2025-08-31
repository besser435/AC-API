package me.besser.commands;

import me.besser.ACAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO: This class is very messy. We should look at how the pros do it, and copy them.
public class CommandHandler implements CommandExecutor, TabCompleter {

    private final BiographyManager bioManager;
    private final int maxBioChars;

    public CommandHandler(ACAPI plugin) {
        this.bioManager = new BiographyManager(plugin);
        this.maxBioChars = plugin.getConfig().getInt("acapi.max_biography_characters");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /" + label + " bio [set|clear|<player>] [text]");
            return true;
        }

        if (args[0].equalsIgnoreCase("bio")) {
            return handleBioCommand(player, args);
        }

        player.sendMessage("§cUnknown subcommand. Try /" + label + " bio");
        return true;
    }

    private boolean handleBioCommand(Player player, String[] args) {
        // /atlas bio - show player's own biography
        if (args.length == 1) {
            return showOwnBiography(player);
        }

        String subCommand = getArg(args, 1);

        // /atlas bio clear
        if (subCommand.equalsIgnoreCase("clear")) {
            return clearBiography(player);
        }

        // /atlas bio set <text>
        if (subCommand.equalsIgnoreCase("set")) {
            return setBiography(player, args);
        }

        // /atlas bio <player> - show another player's biography
        return showPlayerBiography(player, subCommand);
    }

    private boolean showOwnBiography(Player player) {
        String bio = bioManager.getBiography(player);
        if (isEmptyBio(bio)) {
            player.sendMessage("§eYou don't have a biography set. Use §a/atlas bio set <text>§e to set one!");
        } else {
            player.sendMessage("§aYour biography: §f" + bio);
        }
        return true;
    }

    private boolean clearBiography(Player player) {
        bioManager.clearBiography(player);
        player.sendMessage("§aYour biography has been cleared!");
        return true;
    }

    private boolean setBiography(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§cUsage: /atlas bio set <text>");
            return true;
        }

        String biography = joinArgs(args, 2);

        try {
            bioManager.setBiography(player, biography);
            player.sendMessage("§aYour biography has been updated!");
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cYour biography is too long! (Max " + maxBioChars + " characters)");
        }

        return true;
    }

    private boolean showPlayerBiography(Player player, String targetName) {
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            player.sendMessage("§cPlayer not found.");
            return true;
        }

        String bio = bioManager.getBiography(target);
        if (isEmptyBio(bio)) {
            player.sendMessage("§e" + target.getName() + " has no biography set.");
        } else {
            player.sendMessage("§a" + target.getName() + "'s biography: §f" + bio);
        }
        return true;
    }

    // Helper methods
    private String getArg(String[] args, int index) {
        return (args.length > index) ? args[index] : "";
    }

    private String joinArgs(String[] args, int startIndex) {
        if (args.length <= startIndex) {
            return "";
        }
        return String.join(" ", java.util.Arrays.copyOfRange(args, startIndex, args.length));
    }

    private boolean isEmptyBio(String bio) {
        return bio == null || bio.isEmpty();
    }

    // Tab completion
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("bio");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("bio")) {
            return getBioTabCompletions();
        }

        return Collections.emptyList();
    }

    private List<String> getBioTabCompletions() {
        List<String> options = new ArrayList<>();
        options.add("set");
        options.add("clear");

        for (Player online : Bukkit.getOnlinePlayers()) {
            options.add(online.getName());
        }

        return options;
    }
}