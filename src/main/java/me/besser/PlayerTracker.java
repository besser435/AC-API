package me.besser;

import me.besser.commands.BiographyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static me.besser.BesserLogger.*;

public class PlayerTracker implements Listener {
    private final Map<Player, Long> lastMoveTime = new HashMap<>();
    private final Map<Player, Long> joinTime = new HashMap<>();
    private final int AFK_THRESHOLD;
    private final BiographyManager biographyManager;

    public PlayerTracker(ACAPI plugin) {
        this.AFK_THRESHOLD = plugin.getConfig().getInt("acapi.afk_timeout", 180) * 1000;
        this.biographyManager = new BiographyManager(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        long currentTime = Instant.now().toEpochMilli();
        lastMoveTime.put(player, currentTime);
        joinTime.put(player, currentTime);
    }

    // TODO: rename lastMoveTime to lastActivityTime and add more AFK checks like chatting or block entity interactions.
    // Add these changes to TAPI too.
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        lastMoveTime.put(player, Instant.now().toEpochMilli());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        lastMoveTime.remove(player);
        joinTime.remove(player);
    }

    /**
     Returns the AFK duration for a player in milliseconds if they are AFK.
     If the player has moved within the configured AFK threshold, this method returns 0.

     @return The AFK time in seconds. Returns 0 if the player is not AFK.
     */
    public int getPlayerAFKDuration(Player player) {
        long lastMove = lastMoveTime.getOrDefault(player, Instant.now().toEpochMilli());
        long currentTime = Instant.now().toEpochMilli();
        long afkDuration = currentTime - lastMove;

        return afkDuration > AFK_THRESHOLD ? (int) afkDuration : 0;
    }

    /**
     Returns how long a player has been online in milliseconds. Keeps counting while the player is AFK

     @return The total online time in seconds. Returns 0 if the player is not tracked.
     */
    public int getPlayerOnlineDuration(Player player) {
        long joinTimestamp = joinTime.getOrDefault(player, Instant.now().toEpochMilli());
        long currentTime = Instant.now().toEpochMilli();

        return (int) (currentTime - joinTimestamp);
    }

    public Map<String, Object> getOnlinePlayersInfo() {
        Map<String, Object> result = new HashMap<>();

        // Add player information keyed by UUID
        Map<String, Object> playersObject = new HashMap<>();

        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            Map<String, Object> playerData = new HashMap<>();
            String biography = biographyManager.getBiography(player);

            playerData.put("bio", biography != null ? biography : "");
            playerData.put("name", player.getName());
            playerData.put("first_joined", player.getFirstPlayed());
            playerData.put("online_duration", getPlayerOnlineDuration(player));
            playerData.put("afk_duration", getPlayerAFKDuration(player));

            playersObject.put(player.getUniqueId().toString(), playerData);
        }

        result.put("online_players", playersObject);
        return result;
    }
}
