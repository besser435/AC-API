//package me.besser;
//
//import com.google.gson.JsonObject;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.entity.PlayerDeathEvent;
//import org.bukkit.inventory.ItemStack;
//
//import java.util.Objects;
//
//import static me.besser.BesserLogger.*;
//
//public class PVPTracker implements Listener {
//
//    private final ACAPI plugin;
//
//    public PVPTracker(ACAPI plugin) {
//        this.plugin = plugin;
//        plugin.getServer().getPluginManager().registerEvents(this, plugin);
//    }
//
//    @EventHandler
//    public void onPlayerDeath(PlayerDeathEvent event) {
//        Player victim = event.getEntity();
//        Player killer = victim.getKiller();
//
//        if (killer == null) return;
//
//        log(INFO, victim.getName() + " was killed by " + killer.getName());
//
//        ItemStack weapon = killer.getInventory().getItemInMainHand();
//        if (weapon.getType().isItem()) {
//            log(INFO, "Weapon Type: " + weapon.getType().name());
//
//
//            if (weapon.hasItemMeta() && Objects.requireNonNull(weapon.getItemMeta()).hasEnchants()) {
//                weapon.getEnchantments().forEach((enchant, level) -> {
//                    log(INFO, "Enchantment: " + enchant.toString() + " lvl " + level);
//
//                    // TODO: the snipped below will only work if item has enchantments. redo this method
//                    // once all the data we want is figured out.
//                    log(INFO, "Weapon Name: " + weapon.getItemMeta().getDisplayName());
//
//                });
//            }
//        }
//
//        log(WARNING, "deathMessage: " + event.getDeathMessage());
//        log(WARNING, "damageSource: " + event.getDamageSource().toString());
//    }
//
//
//    public JsonObject getPVPData(Player player) {
//        JsonObject result = new JsonObject();
//
//
//        return result;
//    }
//}
