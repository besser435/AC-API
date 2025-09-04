package me.besser.commands;

import me.besser.ACAPI;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BiographyManager {

    private final NamespacedKey bioKey;
    private final int maxBioChars;

    public BiographyManager(ACAPI plugin) {
        this.bioKey = new NamespacedKey(plugin, "bio");
        this.maxBioChars = plugin.getConfig().getInt("acapi.max_biography_characters");
    }

    /**
     Gets a player's biography. They must be online.
     **/
    public String getBiography(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        return data.get(bioKey, PersistentDataType.STRING);
    }

    /**
     Sets a player's biography. They must be online, and the bio must be shorter than the configured limit,
     otherwise an IllegalArgumentException will be thrown.
     **/
    public void setBiography(Player player, String biography) {
        if (biography.length() > maxBioChars) {
            throw new IllegalArgumentException("Biography too long");
        }

        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(bioKey, PersistentDataType.STRING, biography);
    }

    /**
     Removes a player's biography from their persistent data container.
     **/
    public void clearBiography(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.remove(bioKey);
    }
}
