package me.besser;

import me.besser.commands.CommandHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static me.besser.BesserLogger.*;

public final class ACAPI extends JavaPlugin {
    private EndpointServer endpointServer;

    @Override
    public void onEnable() {
        BesserLogger.initialize(this);
        saveDefaultConfig();

        // Check if enabled
        boolean isEnabledInConfig = getConfig().getBoolean("acapi.enable", true);
        if (!isEnabledInConfig) {
            log(WARNING, "ACAPI is disabled in config.yml and will not start.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Set up commands
        CommandHandler handler = new CommandHandler(this);

        Objects.requireNonNull(this.getCommand("atlas")).setExecutor(handler);
        Objects.requireNonNull(this.getCommand("atlas")).setTabCompleter(handler);

        Objects.requireNonNull(this.getCommand("ac")).setExecutor(handler);
        Objects.requireNonNull(this.getCommand("ac")).setTabCompleter(handler);

        // Create shared tracker objects
        // TODO: expose these with getters so we can use their methods in other files without creating new ones,
        // which might cause issues in the future.
        PlayerTracker playerTracker = new PlayerTracker(this);
        PlayerStatTracker playerStatTracker = new PlayerStatTracker();
        ServerInfoTracker serverInfoTracker = new ServerInfoTracker(this);
        PVPTracker PVPTracker = new PVPTracker(this);

        // Initialize the API server and pass shared objects
        this.endpointServer = new EndpointServer(
            this,
            playerTracker,
            playerStatTracker,
            serverInfoTracker,
            PVPTracker
        );

        log(INFO, "AtlasCivs API v" + getDescription().getVersion() + " started!");
    }

    @Override
    public void onDisable() {
        if (endpointServer != null) {
            endpointServer.stop();
        }

        log(INFO, "AtlasCivs API v" + getDescription().getVersion() + " stopped!");
    }
}
