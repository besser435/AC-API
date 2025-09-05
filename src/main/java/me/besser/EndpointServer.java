package me.besser;

import io.javalin.Javalin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;
import java.util.UUID;

import static me.besser.BesserLogger.*;

public class EndpointServer {
    private final ACAPI plugin;
    private final PlayerTracker playerTracker;
    private final PlayerStatTracker playerStatTracker;
    private final ServerInfoTracker serverInfoTracker;
    private final PVPTracker pvpTracker;

    private Javalin app;

    public EndpointServer(
        ACAPI plugin, PlayerTracker playerTracker,
        PlayerStatTracker playerStatTracker, ServerInfoTracker serverInfoTracker, PVPTracker pvpTracker
    ){
        this.plugin = plugin;
        this.playerTracker = playerTracker;
        this.playerStatTracker = playerStatTracker;
        this.serverInfoTracker = serverInfoTracker;
        this.pvpTracker = pvpTracker;

        initRoutes();
    }

    private void initRoutes() {
        // Should maybe add a short TTL cache in the event this becomes more widely used than for just one fetcher.
        FileConfiguration config = plugin.getConfig();
        int serverPort = config.getInt("server.port", 9007);

        app = Javalin.create(javalinConfig -> {
            javalinConfig.http.defaultContentType = "application/json";
            javalinConfig.showJavalinBanner = false;
        });

        app.get("/api/online_players", ctx -> {
            ctx.json(playerTracker.getOnlinePlayersInfo());
        });

        app.get("/api/full_player_stats/{uuid}", ctx -> {   // Requires the player to be online
            String uuidParam = ctx.pathParam("uuid");
            try {
                Player player = Bukkit.getPlayer(UUID.fromString(uuidParam));

                if (player == null || !player.isOnline()) {
                    ctx.status(404).json(Map.of("error", "Player not found or offline"));
                    return;
                }

                ctx.json(playerStatTracker.getPlayerStatistics(player));
            } catch (IllegalArgumentException e) {
                ctx.status(400).json(Map.of("error", "UUID malformed"));
            }
        });

        app.get("/api/server_info", ctx -> {
            Map<String, Object> serverInfo = serverInfoTracker.getServerInfo();
            ctx.json(serverInfo);
        });

        app.get("/api/kill_history", ctx -> {
            String timeParam = ctx.queryParam("time");  // Using query param as it's optional

            // Get the messages, or if provided, only the ones after a certain timestamp
            long timeFilter = 0;
            if (timeParam != null) {
                try {
                    timeFilter = Long.parseLong(timeParam);
                } catch (NumberFormatException e) {
                    ctx.status(400).json(Map.of("error", "Invalid time format, expected Unix epoch in milliseconds"));
                    return;
                }
            }

            long finalTimeFilter = timeFilter;
            ctx.json(
                pvpTracker.getLastKills().stream()
                    .filter(kill -> kill.timestamp() > finalTimeFilter)
                    .toList()
            );
        });

        // Error handling
        app.error(404, ctx -> {
            ctx.json(Map.of("error", "Not found"));
        });

        app.error(500, ctx -> {
            log(WARNING, "Internal server error serving request");
            ctx.json(Map.of("error", "Internal server error"));
        });

        app.start(serverPort);
    }

    public void stop() {
        if (app != null) {
            app.stop();
            log(INFO, "Javalin server stopped");
        }
    }
}
