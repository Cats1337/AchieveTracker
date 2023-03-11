package io.github.cats1337;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AchievePoints {
    // points system
    // points are given for advancements

    // 5 points for regular "task" advancements
    // 10 points for goals
    // 15 points for challenges

    // store the points in an arraylist
    private static Map<String, Integer> points = new HashMap<String, Integer>();

    // static Player getPlayer(String playerName) {
    //     Player player = Bukkit.getPlayer(playerName);
    //     return player;
    // }

    // get the player's points
    public static int getPoints(Player player) {
        String playerName = player.getName();
        if (!points.containsKey(playerName)) {
            return 0;
        }
        return points.get(playerName);
    }

    // give the player points
    public static void addPoints(Player player, int amount) {
        String playerName = player.getName();
        int currentPoints = getPoints(player);
        points.put(playerName, currentPoints + amount);
        player.sendMessage(ChatColor.GREEN + "+" + amount + " points!");
        Main.LOGGER.info(playerName + " now has " + (currentPoints + amount) + " points!");
        // get players total points
        int totalPoints = getPoints(player);
        player.sendMessage(playerName + " now has " + totalPoints + " points!");
    }

    // set the player's points
    public static void setPoints(Player player, int amount) {
        String playerName = player.getName();
        points.put(playerName, amount);
        player.sendMessage(ChatColor.GREEN + "Points set to " + amount + "!");
    }

    // remove the player's points
    public static void removePoints(Player player, int amount) {
        String playerName = player.getName();
        int currentPoints = getPoints(player);
        points.put(playerName, Math.max(0, currentPoints - amount));
        player.sendMessage(ChatColor.GREEN + "-" + amount + " points!");
    }

    // reset the player's points
    public static void resetPoints(Player player) {
        String playerName = player.getName();
        points.put(playerName, 0);
        player.sendMessage(ChatColor.GREEN + "Points reset!");
    }
}