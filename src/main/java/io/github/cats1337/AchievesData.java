package io.github.cats1337;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

// playername, points
public class AchievesData {
    public final HashSet<UUID> previouslyOnlinePlayers;
    public final HashSet<Integer> playerPoints;

    public AchievesData(final HashSet<UUID> previouslyOnlinePlayers, final HashSet<Integer> playerPoints) {
        this.previouslyOnlinePlayers = previouslyOnlinePlayers;
        this.playerPoints = playerPoints;
    }

    public AchievesData(final AchievesData loadedData) {
        this.playerPoints = loadedData.playerPoints;
        this.previouslyOnlinePlayers = loadedData.previouslyOnlinePlayers;
    }

    // check if file exists, if it does, load it if file doesn't exist, create it
    public static void checkFile() {
        if (new File("Achieves.txt").exists()) {
            loadData();
        } else {
            saveData();
        }
    }

    public static void saveData() {
        final Map<UUID, Integer> pointsMap = new HashMap<>();
        // Load existing data
        try (final BufferedReader in = new BufferedReader(new FileReader("Achieves.txt"))) {
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
        try (final BufferedWriter out = new BufferedWriter(new FileWriter("Achieves.txt"))) {
            for (Map.Entry<UUID, Integer> entry : pointsMap.entrySet()) {
                final UUID playerId = entry.getKey();
                final int points = entry.getValue();
                out.write(playerId + "," + points);
                out.newLine();
            }
            Main.LOGGER.log(Level.INFO, "Data saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("Achieves.txt"))) {
            String line;
            HashSet<UUID> previouslyOnlinePlayers = new HashSet<>();
            HashMap<UUID, Integer> playerPoints = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                UUID playerId = UUID.fromString(tokens[0].trim());
                int points = Integer.parseInt(tokens[1].trim());
                previouslyOnlinePlayers.add(playerId);
                playerPoints.put(playerId, points);
                System.out.println("UUID | Points " + playerId + " | " + points);
            }
            for (UUID playerId : previouslyOnlinePlayers) {
                Player player = Bukkit.getServer().getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    AchievePoints.joinSet(player, playerPoints.get(playerId));
                }
            }
            Main.LOGGER.log(Level.INFO, "Data loaded");
            System.out.println(previouslyOnlinePlayers.size() + " Previously Online Players");
            System.out.println(playerPoints.size() + " Player Points");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // When player joins, check if they are in the file, if they are, load their points if they aren't, add them to the file
    public static void playerJoined(PlayerJoinEvent event){
        Player player = event.getPlayer();
        UUID playerID = player.getUniqueId();
        try (BufferedReader reader = new BufferedReader(new FileReader("Achieves.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                UUID id = UUID.fromString(tokens[0].trim());
                if (id.equals(playerID)) {
                    int points = Integer.parseInt(tokens[1].trim());
                    loadData();
                    System.out.println("Player: " + player + " | Points: " + points);
                    AchievePoints.joinSet(player, points);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // if player isn't in file, add player to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Achieves.txt", true))) {
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
        try (BufferedReader reader = new BufferedReader(new FileReader("Achieves.txt"))) {
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

        // if not in file, add them to file
        
    }
}