package io.github.cats1337;

import java.util.logging.Logger;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
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

            AchievesData.loadPoints();
            System.out.println("Loaded points from file");

            final AchieveCommands commands = new AchieveCommands();
                this.getCommand("points").setExecutor((CommandExecutor)commands);
                this.getCommand("setpoints").setExecutor((CommandExecutor)commands);
                this.getCommand("addpoints").setExecutor((CommandExecutor)commands);
                this.getCommand("removepoints").setExecutor((CommandExecutor)commands);
                this.getCommand("resetpoints").setExecutor((CommandExecutor)commands);

        }

    
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        LOGGER.info("AchieveTracker is disabled!");
        System.out.println("AchieveTracker is disabled! (*)");
        
        // *****************************
        // save player points to a file
        // *****************************
        // playername, points
        AchievesData.savePoints();
        LOGGER.info("Saved points to file");
        System.out.println("Saved points to file");

        // rollback prevention

    }
    

    /* When a player gets an advancement, give them points
    5 points for regular advancements
    10 points for goals
    15 points for challenges */
    
    /* store the points in a txt file
    when a player types /points, show them their points
    when a player types /points <player>, show them the points of the player
    when a player types /points leaderboard, show them the leaderboard
    when a player types /points leaderboard <page>, show them the leaderboard on the page */

    // display the leaderboard on a hologram

    // @EventHandler
    // public void onAdvancement(org.bukkit.event.player.PlayerAdvancementDoneEvent event) {
    //     // give the player points
    //     // store the points in a txt file
    //     // display the leaderboard on a hologram
    //     Player player = event.getPlayer();

    //     Advancement advancement = event.getAdvancement();
    //     NamespacedKey key = advancement.getKey();
    //     String advancementKey = key.toString();
    //     String advancementKeySplit[] = advancementKey.split("/");
    //     String advancementType = advancementKeySplit[0];
    //     String advancementName = advancementKeySplit[1];
    //     String advancementPath = advancementKeySplit[2];

    //     System.out.println("Player " + player.getName() + " has completed the advancement " + advancementKey + "!");
    //     System.out.println(advancementType + " " + advancementName + " " + advancementPath);
    //     LOGGER.info("Player " + player.getName() + " has completed the advancement " + advancementKey + "!");

    // }
    
}
