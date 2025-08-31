package me.besser;

import com.earth2me.essentials.Essentials;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static me.besser.BesserLogger.*;

public final class ACAPI extends JavaPlugin {
    private static Essentials essentials = null;
    //private ACAPIDatabase database;

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

        // TODO: is Essentials needed for functions other than money?
        if (!setupEssentials()) { // TODO: add any other depends here.
            log(SEVERE, "Dependencies are missing. ACAPI will not start.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Set up commands
        //Objects.requireNonNull(this.getCommand("atlas")).setExecutor(new BiographyCommand());
        Objects.requireNonNull(this.getCommand("atlas")).setExecutor(new BioCommandTest(this));

        // Create shared tracker objects
        PlayerTracker playerTracker = new PlayerTracker(this);
        PlayerStatTracker playerStatTracker = new PlayerStatTracker();
        ServerInfoTracker serverInfoTracker = new ServerInfoTracker(this);

        // Register events for trackers
        getServer().getPluginManager().registerEvents(playerTracker, this);

        // Initialize the API server and pass shared objects
        EndpointServer endpointServer = new EndpointServer(
            this,
            playerTracker,
            playerStatTracker,
            serverInfoTracker
        );

        log(INFO, ChatColor.AQUA + "AtlasCivs API " + ChatColor.GOLD + "v" + getDescription().getVersion() + ChatColor.RESET + " started!");
    }

    private boolean setupEssentials() {
        Plugin essentialsPlugin = getServer().getPluginManager().getPlugin("Essentials");
        if (essentialsPlugin instanceof Essentials) {
            essentials = (Essentials) essentialsPlugin;
            return true;
        } else {
            log(SEVERE, "Essentials plugin not found!");
            return false;
        }
    }

    public static Essentials getEssentials() {
        return essentials;
    }

    @Override
    public void onDisable() {
        log(INFO, ChatColor.AQUA + "AtlasCivs API " + ChatColor.GOLD + "v" + getDescription().getVersion() + ChatColor.RESET + " stopped!");

        // No chatTracker shutdown message, as that message would be lost on the restart,
        // and is unlikely to be fetched by the API before the server goes down.
    }
}