package io.github.cats1337;

import java.util.logging.Logger;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
// import org.bukkit.plugin.Plugin;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
    static final Logger LOGGER = Logger.getLogger("achievetracker");

    @Override
    public void onEnable() {
        // Plugin startup logic
            LOGGER.info("AchieveTracker is enabled!");
            System.out.println("AchieveTracker is enabled! (*)");
            getServer().getPluginManager().registerEvents(this, (Plugin)this);
            getServer().getPluginManager().registerEvents(new AdvancementListener(), this);

            // makes sure the file exists
            AchievesData.checkFile();
            System.out.println("Checked file");
            // make sure file not null

            AchievesData.loadData();
            System.out.println("Loaded points from file");

            final AchieveCommands commands = new AchieveCommands();
                this.getCommand("points").setExecutor((CommandExecutor)commands);
                this.getCommand("setpoints").setExecutor((CommandExecutor)commands);
                this.getCommand("addpoints").setExecutor((CommandExecutor)commands);
                this.getCommand("removepoints").setExecutor((CommandExecutor)commands);
                this.getCommand("resetpoints").setExecutor((CommandExecutor)commands);
        }

    
    // Plugin shutdown logic
    @Override
    public void onDisable() {
        LOGGER.info("AchieveTracker is disabled!");
        AchievesData.saveData();
        LOGGER.info("Saved points to file");
    }
    
    // When player joins, give them their points back
    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        AchievesData.playerJoined(event);  
    }

    // When player leaves, save their points
    @EventHandler
    public static void onPlayerLeave(PlayerQuitEvent event) {
        AchievesData.playerLeft(event);  
    }
}
