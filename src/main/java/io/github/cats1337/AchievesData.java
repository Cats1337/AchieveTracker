package io.github.cats1337;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import org.bukkit.*;
import org.bukkit.entity.Player;


// playername, points
public class AchievesData implements Serializable {
    private static final transient long serialVersionUID = -1681012206529286330L;
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
        if (new AchievesData(loadData("Achieves.txt")) != null) {
            return;
        }
    }

    public boolean saveData(final String filePath) {
        try {
            try (final BufferedWriter out = new BufferedWriter(
                    new FileWriter(filePath))) {
                out.write(this.previouslyOnlinePlayers.toString());
                out.newLine();
                out.write(this.playerPoints.toString());
                out.newLine();
                out.close();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static AchievesData loadData(final String filePath) {
        try {
            try (final BufferedReader in = new BufferedReader(
                    new FileReader(filePath))) {
                final HashSet<UUID> previouslyOnlinePlayers = new HashSet<>();
                final HashSet<Integer> playerPoints = new HashSet<>();
                String line = in.readLine();
                if (line != null) {
                    String[] uuids = line.substring(1, line.length() - 1).split(", ");
                    for (String uuid : uuids) {
                        previouslyOnlinePlayers.add(UUID.fromString(uuid));
                    }
                }
                line = in.readLine();
                if (line != null) {
                    String[] points = line.substring(1, line.length() - 1).split(", ");
                    for (String point : points) {
                        playerPoints.add(Integer.parseInt(point));
                    }
                }
                in.close();
                return new AchievesData(previouslyOnlinePlayers, playerPoints);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void savePoints() {
        final HashSet<Integer> playerPoints = new HashSet<>();
        final HashSet<UUID> previouslyOnlinePlayers = new HashSet<>();
        Bukkit.getServer().getOnlinePlayers()
                .forEach(player -> previouslyOnlinePlayers.add(player.getUniqueId()));
        Bukkit.getServer().getOnlinePlayers()
                .forEach(player -> playerPoints.add(AchievePoints.getPoints(player)));

        // add testing data
        previouslyOnlinePlayers.add(UUID.randomUUID());
        playerPoints.add(100);

        // currnetly saves as [UUID] \n [points]
        // want [UUID, points]

        new AchievesData(previouslyOnlinePlayers, playerPoints).saveData("Achieves.txt");
        Main.LOGGER.log(Level.INFO, "Data Saved");
        System.out.println(previouslyOnlinePlayers + " Previously Online Players");
        System.out.println(playerPoints + " Player Points");

    }

public static void loadPoints() {
    try (BufferedReader reader = new BufferedReader(new FileReader("Achieves.txt"))) {
        String line;
        HashSet<UUID> previouslyOnlinePlayers = new HashSet<>();
        HashSet<Integer> playerPoints = new HashSet<>();
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split(",");
            previouslyOnlinePlayers.add(UUID.fromString(tokens[0]));
            playerPoints.add(Integer.parseInt(tokens[1]));
        }
        previouslyOnlinePlayers.forEach(playerId -> {
            Player player = Bukkit.getServer().getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.sendMessage("Thanks for coming back after downtime. Hope you see the spawn blocks change!");
            }
        });
        playerPoints.forEach(points -> AchievePoints.setPoints(Bukkit.getServer().getPlayer(previouslyOnlinePlayers.iterator().next()), points));
        Main.LOGGER.log(Level.INFO, "Data loaded");
        System.out.println(previouslyOnlinePlayers + " Previously Online Players");
        System.out.println(playerPoints + " Player Points");
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}