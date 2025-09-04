package me.besser;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class PVPTracker implements Listener {
    private static final int MAX_RECORDS = 50;
    private final List<PVPKill> killHistory = new LinkedList<>();

    public PVPTracker(ACAPI plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public synchronized List<PVPKill> getLastKills() {
        return new LinkedList<>(killHistory); // Return a copy to prevent modification
    }

    public synchronized void addKill(PVPKill kill) {
        if (killHistory.size() >= MAX_RECORDS) {
            killHistory.remove(0);
        }
        killHistory.add(kill);
    }

    public record PVPKill(
        String killer_uuid, String killer_name,
        String victim_uuid, String victim_name,
        String death_message,
        JsonObject weapon,
        long timestamp
    ) {}

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) return;

        ItemStack weapon = killer.getInventory().getItemInMainHand();
        ItemMeta meta = weapon.getItemMeta();

        // Convert weapon info to JSON
        JsonObject weaponJson = new JsonObject();
        weaponJson.addProperty("type", weapon.getType().name().toLowerCase());

        if (meta != null) {
            // Display name
            if (meta.hasDisplayName()) {
                String displayName = meta.getDisplayName();
                weaponJson.addProperty("name", displayName);
            }

            // Enchantments
            if (meta.hasEnchants()) {
                JsonArray enchants = new JsonArray();
                weapon.getEnchantments().forEach((enchant, level) -> {
                    JsonObject enchantJson = new JsonObject();
                    enchantJson.addProperty("id", enchant.getKey().getKey());
                    enchantJson.addProperty("level", level);
                    enchants.add(enchantJson);
                });
                weaponJson.add("enchantments", enchants);
            }
        }

        addKill(new PVPKill(
            killer.getUniqueId().toString(), killer.getName(),
            victim.getUniqueId().toString(), victim.getName(),
            ChatColor.stripColor(event.getDeathMessage()),  // Remove minecraft control codes
            weaponJson,
            Instant.now().toEpochMilli()
        ));
    }
}
