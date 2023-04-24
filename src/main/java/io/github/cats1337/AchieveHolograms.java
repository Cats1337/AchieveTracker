package io.github.cats1337;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.Position;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import net.md_5.bungee.api.ChatColor;

public class AchieveHolograms{
    public static Plugin plugin = AchieveMain.getPlugin(AchieveMain.class); // The plugin instance
    static Location where = new Location(Bukkit.getWorld("world"), Bukkit.getWorld("world").getSpawnLocation().getX(), Bukkit.getWorld("world").getSpawnLocation().getY() + 5, Bukkit.getWorld("world").getSpawnLocation().getZ()); // the location of the hologram (spawn)
    static HolographicDisplaysAPI api = HolographicDisplaysAPI.get(plugin); // The API instance for your plugin
    static Hologram hologram = api.createHologram(where); // Create a new hologram at the given location
    
    public static Position getHologramLocation() {
        return hologram.getPosition();
    }

    public static void createPlaceHologram() {
        hologram.getLines().appendText(AchieveData.PLB);

        ArrayList<String> top10 = AchieveData.getTop10();

        for(int i = 0; i < Math.min(top10.size(), 11); i++) {
            if (top10.get(i) == null) {
                hologram.getLines().appendText(ChatColor.translateAlternateColorCodes('&', "&e" + (i + 1) + ". &a  &8- &6 "));
            } else {
                hologram.getLines().appendText(top10.get(i));
            }
        }

        AchieveData.checkHologramFile();
    }


    public static void updateHologram() {
        AchieveData.saveAchieveData(); // save the data, updating it
        try {
            AchieveData.sortAchieveData();
        } catch (Exception e) {
            e.printStackTrace();
        } // sort the data
        AchieveData.getTop10(); // get the top 10
        // get total hologram lines
        int hls = hologram.getLines().size(); // should be Title + 10 players + 1 player's place = 12 (0-11)

        if (hls != 12) { // debugger, but leaving here just in case
            // remove all lines
            hologram.getLines().clear();
            // create the hologram again
            createPlaceHologram();
        } else {
            // update the lines
            ArrayList<String> top10 = AchieveData.getTop10();
            for(int i = 0; i < Math.min(top10.size(), 11); i++) {
                if (top10.get(i) == null) {
                    hologram.getLines().remove(i+1);
                    hologram.getLines().insertText(i + 1, ChatColor.translateAlternateColorCodes('&', "&e" + (i + 1) + ". &a  &8- &6 "));
                } else {
                    hologram.getLines().remove(i+1);
                    hologram.getLines().insertText(i + 1, top10.get(i));
                }
            }
        }
    
        // VisibilitySettings vis = hologram.getVisibilitySettings();
        // Position hologramPosition = hologram.getPosition();
        // World world = Bukkit.getWorld(hologramPosition.getWorldName());
        // Location location = hologramPosition.toLocation();
        // if (world != null) {
        //     double x = location.getX();
        //     double y = location.getY();
        //     double z = location.getZ();
        //     int radius = 5; // adjust as needed
        //     List<Player> nearbyPlayers = world.getNearbyEntities(location, radius, radius, radius).stream()
        //             .filter(entity -> entity instanceof Player).map(entity -> (Player) entity)
        //             .collect(Collectors.toList());
        //     if (!nearbyPlayers.isEmpty()) {
        //         Player player = nearbyPlayers.get(0); // get the closest player
        //         vis.setIndividualVisibility(player, Visibility.VISIBLE);
        //         vis.setGlobalVisibility(Visibility.HIDDEN);
        //         AchieveMain.LOGGER.info("Player " + player.getName() + " is looking at the hologram");
        //         hologram.getLines().appendText(player.getName());
        //         // show only the last line to the player looking at the hologram
        //         vis.setIndividualVisibility(player, Visibility.VISIBLE);
        //         vis.setGlobalVisibility(Visibility.HIDDEN);
        //     }
        // }

        
    }

    // align the hologram to the nearest block
    public static void alignHologram() { // might add int value to custom round
        // get the hologram's location
        Position pos = hologram.getPosition();
        // get the x, y, z coordinates
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        // round the coordinates
        double roundedX = Math.round(x * 4) / 4.0; // round to nearest .25
        double roundedY = Math.round(y); // round to nearest whole number
        double roundedZ = Math.round(z * 4) / 4.0; // round to nearest .25

        // create a new location with the rounded coordinates
        Location roundLoc = new Location(Bukkit.getWorld("world"), roundedX, roundedY, roundedZ);
        hologram.setPosition(roundLoc);
    }

    // update hologram every 60 seconds
    public static void updateHologramEvery60Seconds() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                updateHologram();
                // AchieveMain.LOGGER.info("Hologram updated - 60 sec"); // debug message
            }
        }, 0L, 1200L); // 1200 ticks = 60 seconds
    }
}
