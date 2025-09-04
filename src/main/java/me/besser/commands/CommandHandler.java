package me.besser.commands;

import me.besser.ACAPI;
import me.besser.ServerInfoTracker;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.besser.BesserLogger.*;


// TODO: This class is very messy. We should look at how the pros do it, and copy them.
// its so bad oh my god


public class CommandHandler implements CommandExecutor, TabCompleter {

    private final BiographyManager bioManager;
    private final int maxBioChars;
    private final ACAPI plugin;

    public CommandHandler(ACAPI plugin) {
        this.bioManager = new BiographyManager(plugin);
        this.maxBioChars = plugin.getConfig().getInt("acapi.max_biography_characters");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {   // breaks about command in console. again, redo this method.
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /" + label + " bio [set | clear | <player>] [text]");
            player.sendMessage("§cUsage: /" + label + " about");
            return true;
        }

        if (args[0].equalsIgnoreCase("bio")) {
            return handleBioCommand(player, args);
        }

        if (args[0].equalsIgnoreCase("about")) {
            return handlePluginAbout(sender);
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

    private boolean handlePluginAbout(CommandSender sender) {
        String name = plugin.getDescription().getName();
        String description = plugin.getDescription().getDescription();
        String version = plugin.getDescription().getVersion();
        String authors = String.join(", ", plugin.getDescription().getAuthors());
        String website = plugin.getDescription().getWebsite();
        ServerInfoTracker serverInfoTracker = new ServerInfoTracker(plugin);    // making a new one is easier for lazy people 🗣️
        //ServerInfoTracker serverInfoTracker = ACAPI.getServerInfoTracker();

        String build = serverInfoTracker.getPluginBuildTime();

        sender.sendMessage(ChatColor.GREEN + "············ " + ChatColor.AQUA + name + ChatColor.GREEN + " ············");
        sender.sendMessage(ChatColor.AQUA + description);
        sender.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.RESET + version);
        sender.sendMessage(ChatColor.YELLOW + "Build: " + ChatColor.RESET + build);
        sender.sendMessage(ChatColor.YELLOW + "Authors: " + ChatColor.RESET + authors);

        TextComponent link = new TextComponent("AC-API GitHub");
        link.setColor(ChatColor.BLUE.asBungee());
        link.setUnderlined(true);
        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, website));

        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(link);
        } else {
            sender.sendMessage(website);
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
            return List.of("about", "bio"); // sure, hard code that shit
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
