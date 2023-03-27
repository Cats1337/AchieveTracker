package io.github.cats1337;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import org.bukkit.plugin.java.JavaPlugin;

public class AchieveMain extends JavaPlugin implements Listener {
    static final Logger LOGGER = Logger.getLogger("achievetracker");
    private boolean useHolographicDisplays;

    @Override
    public void onEnable() {

        useHolographicDisplays = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
       
        if (useHolographicDisplays) {
            LOGGER.info("HolographicDisplays is enabled!");
        } else {
            LOGGER.info("HolographicDisplays is not enabled! Holograms will not work!");
        }
        // Plugin startup logic
            LOGGER.info("AchieveTracker is enabled!");
            getServer().getPluginManager().registerEvents(this, (Plugin)this);
            getServer().getPluginManager().registerEvents(new AdvancementListener(), this);

            // makes sure the file exists
            AchieveData.checkFile();
            try {
                AchieveData.sortData();
            } catch (Exception e) {
                e.printStackTrace();
            }

            AchieveData.loadData();
            System.out.println("Loaded points from file");

            final AchieveCommands commands = new AchieveCommands();
                this.getCommand("points").setExecutor((CommandExecutor)commands);
                this.getCommand("pointsset").setExecutor((CommandExecutor)commands);
                this.getCommand("pointsadd").setExecutor((CommandExecutor)commands);
                this.getCommand("pointsremove").setExecutor((CommandExecutor)commands);
                this.getCommand("pointsreset").setExecutor((CommandExecutor)commands);
                this.getCommand("hologrampoints").setExecutor((CommandExecutor)commands);
                this.getCommand("hologrampointsreload").setExecutor((CommandExecutor)commands);
                this.getCommand("hologrampointsmove").setExecutor((CommandExecutor)commands);
                this.getCommand("hologrampointsmovehere").setExecutor((CommandExecutor)commands);

            AchieveHolograms.createPlaceHologram();

            AchieveHolograms.updateHologramEvery60Seconds();
        }

    
    // Plugin shutdown logic
    @Override
    public void onDisable() {
        LOGGER.info("AchieveTracker is disabled!");
        AchieveData.saveData();
        AchieveData.saveHologramData();
        try {
            AchieveData.sortData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("Saved points to file");
    }
    
    // When player joins, give them their points back
    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        AchieveData.playerJoined(event);  
    }

    // When player leaves, save their points
    @EventHandler
    public static void onPlayerLeave(PlayerQuitEvent event) {
        AchieveData.playerLeft(event);  
    }
}
