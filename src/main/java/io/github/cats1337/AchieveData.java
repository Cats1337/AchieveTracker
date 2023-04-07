package io.github.cats1337;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

public class AchieveData {
    public final HashSet<UUID> previouslyOnlinePlayers;
    public final HashMap<UUID, Integer> playerPoints;

    public AchieveData(final HashSet<UUID> previouslyOnlinePlayers, final HashMap<UUID, Integer> playerPoints) {
        this.previouslyOnlinePlayers = previouslyOnlinePlayers;
        this.playerPoints = playerPoints;
    }

    public AchieveData(final AchieveData loadedData) {
        this.playerPoints = loadedData.playerPoints;
        this.previouslyOnlinePlayers = loadedData.previouslyOnlinePlayers;
    }

    // check if file exists, if it does, load it if file doesn't exist, create it
    public static void checkFile() {
        File file = new File("plugins/AchieveTracker/Achieves.yml");
        if (file.exists()) {
            loadData();
        } else {
            createFile();
        }
    }

    public static void createFile() {
        File folder = new File("plugins/AchieveTracker");
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File("plugins/AchieveTracker/Achieves.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveData() {
        final Map<UUID, Integer> pointsMap = new HashMap<>();
        // Load existing data
        File file = new File("plugins/AchieveTracker/Achieves.yml");
        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (String playerId : config.getKeys(false)) {
                UUID uuid = UUID.fromString(playerId);
                int points = config.getInt(playerId);
                pointsMap.put(uuid, points);
            }
        }

        // Update data with current online players, if they have more points than the
        // existing data
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            final UUID playerId = player.getUniqueId();
            final int currentPoints = AchievePoints.getPoints(player);
            pointsMap.put(playerId, currentPoints);
        });

        // Save data to file (overwriting existing file)
        try {
            YamlConfiguration config = new YamlConfiguration();
            for (Map.Entry<UUID, Integer> entry : pointsMap.entrySet()) {
                final UUID playerId = entry.getKey();
                final int points = entry.getValue();
                config.set(playerId.toString(), points);
            }
            config.save(new File("plugins/AchieveTracker/Achieves.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadData() {
        File file = new File("plugins/AchieveTracker/Achieves.yml");
        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            HashSet<UUID> previouslyOnlinePlayers = new HashSet<>();
            HashMap<UUID, Integer> playerPoints = new HashMap<>();
            for (String playerId : config.getKeys(false)) {
                UUID uuid = UUID.fromString(playerId);
                int points = config.getInt(playerId);
                previouslyOnlinePlayers.add(uuid);
                playerPoints.put(uuid, points);
            }
            for (UUID playerId : previouslyOnlinePlayers) {
                Player player = Bukkit.getServer().getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    AchievePoints.joinSet(player, playerPoints.get(playerId));
                }
            }
        }
    }

    // When player joins, check if they are in the file, if they are, load their
    // points if they aren't, add them to the file
    public static void playerJoined(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerID = player.getUniqueId();
        File file = new File("plugins/AchieveTracker/Achieves.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config.contains(playerID.toString())) {
            int points = config.getInt(playerID.toString());
            AchievePoints.joinSet(player, points);
        } else {
            config.set(playerID.toString(), AchievePoints.getPoints(player));
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // When player leaves, save their points
    public static void playerLeft(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerID = player.getUniqueId();
        File file = new File("plugins/AchieveTracker/Achieves.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config.contains(playerID.toString())) {
            config.set(playerID.toString(), AchievePoints.getPoints(player));
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // sort data in file by points, highest to lowest
    public static void sortData() {
        File file = new File("plugins/AchieveTracker/Achieves.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ArrayList<String> lines = new ArrayList<>();
        for (String playerId : config.getKeys(false)) {
            UUID uuid = UUID.fromString(playerId);
            int points = config.getInt(playerId);
            lines.add(uuid.toString() + "," + points);
        }
        Collections.sort(lines, new Comparator<String>() {
            public int compare(String s1, String s2) {
                String[] tokens1 = s1.split(",");
                String[] tokens2 = s2.split(",");
                int points1 = Integer.parseInt(tokens1[1]);
                int points2 = Integer.parseInt(tokens2[1]);
                return Integer.compare(points2, points1);
            }
        });
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] tokens = line.split(",");
            UUID uuid = UUID.fromString(tokens[0]);
            int points = Integer.parseInt(tokens[1]);
            config.set(uuid.toString(), points);
        }
        
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // get player points from file
    public static int getPlayerPoints(Player player) {
        UUID playerID = player.getUniqueId();
        File file = new File("plugins/AchieveTracker/Achieves.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        int points = config.getInt(playerID.toString());
        return points;
    }

    // get player place from file
    public static int getPlayerPlace(Player player) {
        if (player == null) {
            return 0;
        }
        UUID playerID = player.getUniqueId();
        File file = new File("plugins/AchieveTracker/Achieves.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        int place = config.getInt(playerID.toString() + ".place");
        return place;
    }

    // get top 10 players from file
    public static ArrayList<String> getTop10() {
        ArrayList<String> top10 = new ArrayList<>();
        File file = new File("plugins/AchieveTracker/Achieves.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ArrayList<String> lines = new ArrayList<>();
        for (String playerId : config.getKeys(false)) {
            UUID uuid = UUID.fromString(playerId);
            int points = config.getInt(playerId);
            lines.add(uuid.toString() + "," + points);
        }
        Collections.sort(lines, new Comparator<String>() {
            public int compare(String s1, String s2) {
                String[] tokens1 = s1.split(",");
                String[] tokens2 = s2.split(",");
                int points1 = Integer.parseInt(tokens1[1]);
                int points2 = Integer.parseInt(tokens2[1]);
                return Integer.compare(points2, points1);
            }
        });
        int place = 1;
        for (String line : lines) {
            String[] tokens = line.split(",");
            UUID uuid = UUID.fromString(tokens[0]);
            int points = Integer.parseInt(tokens[1]);
            top10.add(ChatColor.translateAlternateColorCodes('&',
                    "&e" + place + ". &a" + Bukkit.getOfflinePlayer(uuid).getName() + " &7- &6" + points));
            place++;
            if (place == 11) {
                break;
            }
        }
        return top10;
    }

    // get leaderboard from file
public static ArrayList<String> getLeaderboard(Player player) {
    saveData();
    try {
        sortData();
    } catch (Exception e) {
        e.printStackTrace();
    }
    ArrayList<String> leaderboard = new ArrayList<>();
    File file = new File("plugins/AchieveTracker/Achieves.yml");
    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
    int place = 0;
    leaderboard.add(ChatColor.translateAlternateColorCodes('&', "&2&l&nPoints&r &e&l&nLeaderboard"));
    place++;

    ArrayList<String> lines = new ArrayList<>();
    for (String playerId : config.getKeys(false)) {
        UUID uuid = UUID.fromString(playerId);
        int points = config.getInt(playerId);
        lines.add(uuid.toString() + "," + points);
    }
    Collections.sort(lines, new Comparator<String>() {
        public int compare(String s1, String s2) {
            String[] tokens1 = s1.split(",");
            String[] tokens2 = s2.split(",");
            int points1 = Integer.parseInt(tokens1[1]);
            int points2 = Integer.parseInt(tokens2[1]);
            return Integer.compare(points2, points1);
        }
    });

    for (String line : lines) {
        String[] tokens = line.split(",");
        UUID uuid = UUID.fromString(tokens[0]);
        int points = Integer.parseInt(tokens[1]);

        // if player, add with different color
        if (place == getPlayerPlace(player) + 1) {
            leaderboard.add(ChatColor.translateAlternateColorCodes('&', "&e" + place + ". &b&l" + Bukkit.getOfflinePlayer(uuid).getName() + " &7- &6" + points + " &8(You)"));
            place++;
        } else { // if not player, add placeholder
            leaderboard.add(ChatColor.translateAlternateColorCodes('&', "&e" + place + ". &a" + Bukkit.getOfflinePlayer(uuid).getName() + " &7- &6" + points));
            place++;
        }
        if (place == 11) {
            break;
        }
    }
        leaderboard.add(ChatColor.translateAlternateColorCodes('&', "&eYou're in &b" + placeSuffix(player) + "&e place with &b" + AchievePoints.getPoints(player) + "&e points!"));

        return leaderboard;
    }

    public static String placeSuffix(Player player) {
        // get the player's rank
        int playerPlace = getPlayerPlace(player) + 1;

        // determine the suffix to add
        String suffix;
        if (playerPlace % 100 == 11 || playerPlace % 100 == 12 || playerPlace % 100 == 13) {
            suffix = "th";
        } else if (playerPlace % 10 == 1) {
            suffix = "st";
        } else if (playerPlace % 10 == 2) {
            suffix = "nd";
        } else if (playerPlace % 10 == 3) {
            suffix = "rd";
        } else {
            suffix = "th";
        }
        // add the suffix to the player's rank
        String rankWithSuffix = playerPlace + suffix;

        return rankWithSuffix;
    }


    //check if player is in file, if true return their name and points
    public static String playerCheck(String playerName) {
        File file = new File("plugins/AchieveTracker/Achieves.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getName().equalsIgnoreCase(playerName)) {
                UUID playerID = offlinePlayer.getUniqueId();
                if (config.contains(playerID.toString())) {
                    int points = config.getInt(playerID.toString());
                    String player = Bukkit.getOfflinePlayer(playerID).getName();
                    return player + "," + points;
                }
            }
        }
        return null;
    }

    public static boolean checkHologramFile() {
        File file = new File("plugins/AchieveTracker/Holograms.yml");
        return file.exists();
    }

    // create hologram file
public static void createHologramFile() {
    File file = new File("plugins/AchieveTracker/Holograms.yml");
    if (!file.exists()) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// save hologram location in file
public static void saveHologramData() {
    // save x, y, z coordinates of hologram
    Location location = AchieveHolograms.getHologramLocation().toLocation();
    YamlConfiguration config = new YamlConfiguration();
    config.set("worldName", location.getWorld().getName());
    config.set("x", location.getX());
    config.set("y", location.getY());
    config.set("z", location.getZ());

    try {
        config.save(new File("plugins/AchieveTracker/Holograms.yml"));
    } catch (IOException e) {
        e.printStackTrace();
    }
}

// load hologram data from file
public static void loadHologramData() {
    File file = new File("plugins/AchieveTracker/Holograms.yml");
    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

    String worldName = config.getString("worldName");
    double x = config.getDouble("x");
    double y = config.getDouble("y");
    double z = config.getDouble("z");

    AchieveHolograms.hologram.setPosition(new Location(Bukkit.getWorld(worldName), x, y, z));
}


}