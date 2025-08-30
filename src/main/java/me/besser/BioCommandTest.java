package me.besser;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BioCommandTest implements CommandExecutor {

    private final NamespacedKey bioKey;

    public BioCommandTest(ACAPI plugin) {
        this.bioKey = new NamespacedKey(plugin, "bio");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        PersistentDataContainer data = player.getPersistentDataContainer();

         /* TODO: Fix command structure, this is just a prototype.
         These dont work as expected (see bugs below), and are a pain to maintain.
         Have a main CommandHandler class, and just call functions from other classes
         inside that class.

         Current bugs are as follows:
         /atlas returns "your bio: {bio}", should be /atlas bio.
         /atlas bio clear doesnt work. running /atlas shows the bio as "bio clear"
          */

        // No args, show bio
        if (args.length == 0) {
            String bio = data.get(bioKey, PersistentDataType.STRING);
            if (bio == null || bio.isEmpty()) {
                player.sendMessage("§eYou don’t have a biography set. Use §a/atlas bio <text>§e to set one!");
            } else {
                player.sendMessage("§aYour biography: §f" + bio);
            }
            return true;
        }

        // "clear" == delete bio
        if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
            data.remove(bioKey);
            player.sendMessage("§aYour biography has been cleared!");
            return true;
        }

        // Set new biography
        String biography = String.join(" ", args);
        if (biography.length() > 100) {
            player.sendMessage("§cYour biography is too long! (Max 100 characters)");
            return true;
        }

        data.set(bioKey, PersistentDataType.STRING, biography);
        player.sendMessage("§aYour biography has been updated!");
        return true;
    }
}
