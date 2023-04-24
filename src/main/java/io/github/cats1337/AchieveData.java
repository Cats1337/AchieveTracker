package io.github.cats1337;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

public class AchieveData {
    public final HashSet<UUID> previouslyOnlinePlayers;
    public final HashMap<UUID, Integer> playerPoints;
    
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([0-9a-fA-F]){6}");
    static final String PLB = colorizeHex("&#00E259&l&nP&#00D657&l&no&#00CA56&l&ni&#00BF54&l&nn&#00B353&l&nt&#00A751&l&ns&r &#FFE259&l&nL&#FFDC58&l&ne&#FFD657&l&na&#FFD057&l&nd&#FFCA56&l&ne&#FFC555&l&nr&#FFBF54&l&nb&#FFB953&l&no&#FFB353&l&na&#FFAD52&l&nr&#FFA751&l&nd&r");

    public AchieveData(final HashSet<UUID> previouslyOnlinePlayers, final HashMap<UUID, Integer> playerPoints) {
        this.previouslyOnlinePlayers = previouslyOnlinePlayers;
        this.playerPoints = playerPoints;
    }

    public AchieveData(final AchieveData loadedData) {
        this.playerPoints = loadedData.playerPoints;
        this.previouslyOnlinePlayers = loadedData.previouslyOnlinePlayers;
    }

    public static String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static String colorizeHex(String str) {
        Matcher matcher = HEX_PATTERN.matcher(str);
        while (matcher.find()) {
            String group = matcher.group();
            str = str.replace(group, ChatColor.of(group.substring(1)).toString());
        }
        return colorize(str);
    }

    // check if file exists, if it does, load it if file doesn't exist, create it
    public static void checkAchieveFile() {
        File file = new File("plugins/AchieveTracker/Achieves.yml");
        if (file.exists()) {
            loadAchieveData();
        } else {
            createAchieveFile();
        }
    }

    public static void createAchieveFile() {
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

    public static void saveAchieveData() {
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

    public static void loadAchieveData() {
        File file = new File("plugins/AchieveTracker/Achieves.yml");
        if (file.exists()) {
            try {
                sortAchieveData();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            // saveData();
            try {
                sortAchieveData();
            } catch (Exception e) {
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
            // saveData();
            try {
                sortAchieveData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // sort data in file by points, highest to lowest
    public static void sortAchieveData() throws Exception {
        // read data from file into a map of UUIDs and points
        Map<UUID, Integer> data = new HashMap<>();
        File file = new File("plugins/AchieveTracker/Achieves.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String uuidString : config.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidString);
            int points = config.getInt(uuidString);
            data.put(uuid, points);
        }

        // sort map by points descending
        List<Entry<UUID, Integer>> entries = new ArrayList<>(data.entrySet());
        Collections.sort(entries, new Comparator<Entry<UUID, Integer>>() {
            public int compare(Entry<UUID, Integer> entry1, Entry<UUID, Integer> entry2) {
                return Integer.compare(entry2.getValue(), entry1.getValue());
            }
        });

        // save sorted data to file (overwriting existing file)
        config = new YamlConfiguration();
        for (Entry<UUID, Integer> entry : entries) {
            config.set(entry.getKey().toString(), entry.getValue());
        }
        config.save(file);
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
        try {
            sortAchieveData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (player == null) {
            return 0;
        }
        UUID playerID = player.getUniqueId();
        File file = new File("plugins/AchieveTracker/Achieves.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        // get the line number of the player
        int lineNumber = 0;
        for (String playerId : config.getKeys(false)) {
            lineNumber++;
            if (playerId.equals(playerID.toString())) {
                break;
            }
        }
        return lineNumber;
    }

    // get top 10 players from file
    public static ArrayList<String> getTop10() {
        ArrayList<String> top10 = new ArrayList<>();
        File file = new File("plugins/AchieveTracker/Achieves.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> playerIds = new ArrayList<>(config.getKeys(false));
        playerIds.sort((id1, id2) -> Integer.compare(config.getInt(id2), config.getInt(id1)));
        int place = 0;
        // while there are still lines in the file and the place is less than 10
        while (place < playerIds.size() && place < 10) {
            UUID id = UUID.fromString(playerIds.get(place));
            int points = config.getInt(playerIds.get(place));
            top10.add(ChatColor.translateAlternateColorCodes('&',
                    "&e" + (place + 1) + ". &a" + Bukkit.getOfflinePlayer(id).getName() + " &7- &6" + points));
            place++;
            if (place == 11) {
                // get the player looking at the leaderboard
                Player player = Bukkit.getServer().getPlayer(playerIds.get(place));
                // if the player is online, add them to the leaderboard
                if (player != null && player.isOnline()) {
                    top10.add(ChatColor.translateAlternateColorCodes('&', "&e You are in &a" + placeSuffix(player)
                            + "&e place with &a" + getPlayerPoints(player) + "&e points!"));
                }
            }
            if (place == 12) {
                break;
            }
        }
        return top10;
    }

    // get leaderboard from file
    public static ArrayList<String> getLeaderboard(Player player, int page) {
        saveAchieveData();

        try {
            sortAchieveData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<String> leaderboard = new ArrayList<>();
        File file = new File("plugins/AchieveTracker/Achieves.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        int startIndex = (page - 1) * 10; 
        int endIndex = startIndex + 10;
        int place = startIndex + 1;

        leaderboard.add(ChatColor.translateAlternateColorCodes('&', PLB + " &8- Page " + page));

        List<String> keys = new ArrayList<>(yaml.getKeys(false));
        for (int i = startIndex; i < endIndex && i < keys.size(); i++) {
            String key = keys.get(i);
            UUID id = UUID.fromString(key);
            int points = yaml.getInt(key);

            // if player, add with different color
            if (id.equals(player.getUniqueId())) {
                leaderboard.add(ChatColor.translateAlternateColorCodes('&',
                        "&e" + place + ". &b&l" + player.getName() + " &7- &6" + points + " &8(You)"));
                place++;
            } else { // if not player, add placeholder
                leaderboard.add(ChatColor.translateAlternateColorCodes('&',
                        "&e" + place + ". &a" + Bukkit.getOfflinePlayer(id).getName() + " &7- &6" + points));
                place++;
            }
        }
        leaderboard.add(ChatColor.translateAlternateColorCodes('&', "&eYou're in &b" + placeSuffix(player)
                + "&e place with &b" + AchievePoints.getPoints(player) + "&e points!"));

        return leaderboard;
    }

    public static String placeSuffix(Player player) {
        // get the player's rank
        int playerPlace = getPlayerPlace(player);

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

    // check if player is in file, if true return their name and points
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
        if (!file.exists()) {
            createHologramFile();
            AchieveMain.LOGGER.info("Hologram file created");
        } else{
            loadHologramData();
        }

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
        if (config.contains("x") || config.contains("y") || config.contains("z") || config.contains("worldName")) {
            double x = config.getDouble("x");
            double y = config.getDouble("y");
            double z = config.getDouble("z");
            World world = Bukkit.getWorld(config.getString("worldName"));
            if (world != null) {
                Location loc = new Location(world, x, y, z);
                // set the location of the hologram
                AchieveHolograms.hologram.setPosition(loc);
            } else {
                AchieveMain.LOGGER.warning("Invalid world name in hologram file: " + config.getString("worldName"));
            }
        } else {
            AchieveMain.LOGGER.warning("Missing position information in hologram file");
        }
    }

}