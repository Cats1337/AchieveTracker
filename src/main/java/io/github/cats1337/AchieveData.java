package io.github.cats1337;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.md_5.bungee.api.ChatColor;

public class AchieveData {
    public final HashSet<UUID> previouslyOnlinePlayers;
    public final HashSet<Integer> playerPoints;

    public AchieveData(final HashSet<UUID> previouslyOnlinePlayers, final HashSet<Integer> playerPoints) {
        this.previouslyOnlinePlayers = previouslyOnlinePlayers;
        this.playerPoints = playerPoints;
    }

    public AchieveData(final AchieveData loadedData) {
        this.playerPoints = loadedData.playerPoints;
        this.previouslyOnlinePlayers = loadedData.previouslyOnlinePlayers;
    }

    // check if file exists, if it does, load it if file doesn't exist, create it
    public static void checkFile() {
        if (new File("plugins/AchieveTracker/Achieves.txt").exists()) {
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
        File file = new File("plugins/AchieveTracker/Achieves.txt");
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
        try (final BufferedReader in = new BufferedReader(new FileReader("plugins/AchieveTracker/Achieves.txt"))) {
            String line;
            while ((line = in.readLine()) != null) {
                final String[] parts = line.split(",");
                final UUID playerId = UUID.fromString(parts[0]);
                final int points = Integer.parseInt(parts[1]);
                pointsMap.put(playerId, points);
            }
        } catch (IOException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        // Update data with current online players, if they have more points than the existing data
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            final UUID playerId = player.getUniqueId();
            final int currentPoints = AchievePoints.getPoints(player);
                pointsMap.put(playerId, currentPoints);
        });

        // Save data to file (overwriting existing file)
        try (final BufferedWriter out = new BufferedWriter(new FileWriter("plugins/AchieveTracker/Achieves.txt"))) {
            for (Map.Entry<UUID, Integer> entry : pointsMap.entrySet()) {
                final UUID playerId = entry.getKey();
                final int points = entry.getValue();
                out.write(playerId + "," + points);
                out.newLine();
            }
            // AchieveMain.LOGGER.log(Level.INFO, "Data saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("plugins/AchieveTracker/Achieves.txt"))) {
            String line;
            HashSet<UUID> previouslyOnlinePlayers = new HashSet<>();
            HashMap<UUID, Integer> playerPoints = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                UUID playerId = UUID.fromString(tokens[0].trim());
                int points = Integer.parseInt(tokens[1].trim());
                previouslyOnlinePlayers.add(playerId);
                playerPoints.put(playerId, points);
            }
            for (UUID playerId : previouslyOnlinePlayers) {
                Player player = Bukkit.getServer().getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    AchievePoints.joinSet(player, playerPoints.get(playerId));
                }
            }
            
            // AchieveMain.LOGGER.info("Data loaded");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // When player joins, check if they are in the file, if they are, load their points if they aren't, add them to the file
    public static void playerJoined(PlayerJoinEvent event){
        Player player = event.getPlayer();
        UUID playerID = player.getUniqueId();
        try (BufferedReader reader = new BufferedReader(new FileReader("plugins/AchieveTracker/Achieves.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                UUID id = UUID.fromString(tokens[0].trim());
                if (id.equals(playerID)) {
                    int points = Integer.parseInt(tokens[1].trim());
                    // loadData();
                    // System.out.println("Player: " + player + " | Points: " + points);
                    AchievePoints.joinSet(player, points);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // if player isn't in file, add player to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("plugins/AchieveTracker/Achieves.txt", true))) {
            writer.write(playerID + "," + AchievePoints.getPoints(player));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // When player leaves, save their points
    public static void playerLeft(PlayerQuitEvent event){
        Player player = event.getPlayer();
        UUID playerID = player.getUniqueId();
        // check if player is in file, if they are, overwrite their points
        try (BufferedReader reader = new BufferedReader(new FileReader("plugins/AchieveTracker/Achieves.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                UUID id = UUID.fromString(tokens[0].trim());
                if (id.equals(playerID)) {
                    saveData();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // sort data in file by points, highest to lowest
    public static void sortData() throws Exception {
        // read data from file into a list of strings
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("plugins/AchieveTracker/Achieves.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }

        // sort list by points descending
        Collections.sort(lines, new Comparator<String>() {
            public int compare(String s1, String s2) {
                String[] tokens1 = s1.split(",");
                String[] tokens2 = s2.split(",");
                int points1 = Integer.parseInt(tokens1[1]);
                int points2 = Integer.parseInt(tokens2[1]);
                return Integer.compare(points2, points1);
            }
        });

        // save sorted data to file (overwriting existing file)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("plugins/AchieveTracker/Achieves.txt"))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }

    }

    // get player points from file
    public static int getPlayerPoints(Player player) {
        UUID playerID = player.getUniqueId();
        try (BufferedReader reader = new BufferedReader(new FileReader("plugins/AchieveTracker/Achieves.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                UUID id = UUID.fromString(tokens[0].trim());
                if (id.equals(playerID)) {
                    int points = Integer.parseInt(tokens[1].trim());
                    return points;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // get player place from file
    public static int getPlayerPlace(Player player) {
        if (player == null) {
            // System.out.println(player);
            return 0;
        }
        UUID playerID = player.getUniqueId();
        try (BufferedReader reader = new BufferedReader(new FileReader("plugins/AchieveTracker/Achieves.txt"))) {
            String line;
            int place = 0;
            while((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                UUID id = UUID.fromString(tokens[0].trim());
                if (id.equals(playerID)) {
                    return place;
                }
                place++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // get top 10 players from file
    public static ArrayList<String> getTop10() {
        ArrayList<String> top10 = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("plugins/AchieveTracker/Achieves.txt"))) {
            String line;
            int place = 1;
            while((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                UUID id = UUID.fromString(tokens[0].trim());
                int points = Integer.parseInt(tokens[1].trim());
                top10.add(ChatColor.translateAlternateColorCodes('&', "&e" + place + ". &a" + Bukkit.getOfflinePlayer(id).getName() + " &7- &6" + points));
                place++;
                if (place == 11) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
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
        try (BufferedReader reader = new BufferedReader(new FileReader("plugins/AchieveTracker/Achieves.txt"))) {
            String line;
            int place = 0;
            leaderboard.add(ChatColor.translateAlternateColorCodes('&', "&2&l&nPoints&r &e&l&nLeaderboard"));
            place++;
            
            while((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                UUID id = UUID.fromString(tokens[0].trim());
                int points = Integer.parseInt(tokens[1].trim());

                // if player, add with different color
                if (place == getPlayerPlace(player) + 1) {
                    leaderboard.add(ChatColor.translateAlternateColorCodes('&', "&e" + place + ". &b&l" + Bukkit.getOfflinePlayer(id).getName() + " &7- &6" + points + " &8(You)"));
                    place++;
                } else { // if not player, add placeholder
                    leaderboard.add(ChatColor.translateAlternateColorCodes('&', "&e" + place + ". &a" + Bukkit.getOfflinePlayer(id).getName() + " &7- &6" + points));
                    place++;
                }
                if (place == 11) {
                    break;
                }
            }
            leaderboard.add(ChatColor.translateAlternateColorCodes('&', "&eYou're in &b" + placeSuffix(player) + "&e place with &b" + AchievePoints.getPoints(player) + "&e points!"));

        } catch (IOException e) {
            e.printStackTrace();
        }
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
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getName().equalsIgnoreCase(playerName)) {
                UUID playerID = offlinePlayer.getUniqueId();
                try (BufferedReader reader = new BufferedReader(
                        new FileReader("plugins/AchieveTracker/Achieves.txt"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] tokens = line.split(",");
                        UUID id = UUID.fromString(tokens[0].trim());
                        if (id.equals(playerID)) {
                            // convert UUID to player name
                            String players = Bukkit.getOfflinePlayer(id).getName();
                            int points = Integer.parseInt(tokens[1].trim());
                            return players + "," + points;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static boolean checkHologramFile() {
        if (new File("plugins/AchieveTracker/Holograms.txt").exists()) {
            loadHologramData();
            return true;
        } else {
            createHologramFile();
            return false;
        }
    }

    // create hologram file
    public static void createHologramFile() {
        File file = new File("plugins/AchieveTracker/Holograms.txt");
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
        String position = AchieveHolograms.getHologramLocation().toString();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("plugins/AchieveTracker/Holograms.txt"))) {
            // bw.write(x + "," + y + "," + z);
            bw.write(position);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // load hologram data from file
    public static void loadHologramData(){
        try (BufferedReader reader = new BufferedReader(new FileReader("plugins/AchieveTracker/Holograms.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                // remove Position{worldName= from the string
                String worldName = tokens[0].trim().substring(19);
                double x = Double.parseDouble(tokens[1].trim().substring(2));
                double y = Double.parseDouble(tokens[2].trim().substring(2));
                // remove the } at the end of the string
                double z = Double.parseDouble(tokens[3].trim().substring(2, tokens[3].trim().length() - 1));

                AchieveHolograms.hologram.setPosition(new Location(Bukkit.getWorld(worldName), x, y, z));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}