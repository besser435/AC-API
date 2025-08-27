package me.besser;

import static spark.Spark.*;
import com.google.gson.Gson;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.besser.BesserLogger.*;

public class EndpointServer {
    // TODO: Spark is not maintained anymore. Transition to Javalin.

    private final JavaPlugin plugin;
    private final PlayerTracker playerTracker;
    private final PlayerStatTracker playerStatTracker;
    private final ServerInfoTracker serverInfoTracker;
    private final Gson gson = new Gson();

    public EndpointServer(  // TODO: is there a better way to pass objects?
        JavaPlugin plugin, PlayerTracker playerTracker,
        PlayerStatTracker playerStatTracker, ServerInfoTracker serverInfoTracker
    ){
        this.plugin = plugin;
        this.playerTracker = playerTracker;
        this.playerStatTracker = playerStatTracker;
        this.serverInfoTracker = serverInfoTracker;

        initRoutes();
    }

    private void initRoutes() {
        // Should maybe add a short TTL cache
        FileConfiguration config = plugin.getConfig();
        int serverPort = config.getInt("server.port", 1850);

        port(serverPort);

        get("/api/online_players", (request, response) -> {
            response.type("application/json");

            return gson.toJson(playerTracker.getOnlinePlayersInfo());
        });

        get("/api/full_player_stats/:uuid", (request, response) -> {    // Requires the player to be online
            String uuidParam = request.params("uuid");
            response.type("application/json");

            try {
                Player player = Bukkit.getPlayer(UUID.fromString(uuidParam));

                if (player == null || !player.isOnline()) {
                    response.status(404);
                    return gson.toJson(Map.of("error", "Player not found or offline"));
                }

                return gson.toJson(playerStatTracker.getPlayerStatistics(player));

            } catch (IllegalArgumentException e) {
                return gson.toJson(Map.of("error", "UUID malformed"));
            }
        });

        get("/api/server_info", (request, response) -> {
            response.type("application/json");

            Map<String, Object> serverInfo = serverInfoTracker.getServerInfo();
            return gson.toJson(serverInfo);
        });

        notFound((req, res) -> {
            res.type("application/json");
            res.status(404);
            return gson.toJson(Map.of("error", "Not found"));
        });

        internalServerError((req, res) -> {
            res.type("application/json");
            res.status(500);
            log(WARNING, "Internal server error serving request" );
            return gson.toJson(Map.of("error", "Internal server error"));
        });

        // BUG, when the player is offline this takes ~8s to respond, and returns all data, not just the general stats. this wasn't the case before...
//        get("/api/offline_player_stats/:uuid", (req, res) -> {
//            String uuid = req.params("uuid");
//            res.type("application/json");
//
//            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
//            if (player == null) {
//                res.status(404);
//                return gson.toJson("Player not found");
//            }
//
//            return gson.toJson(playerStatTracker.getPlayerStatistics(player));
//        });
    }
}
