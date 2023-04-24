package io.github.cats1337;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class AchievePoints {
    // store the points in an arraylist
    private static Map<String, Integer> points = new HashMap<String, Integer>();

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
    }

    // set the player's points
    public static void setPoints(Player player, int amount) {
        if(player != null) {
            String playerName = player.getName();
            points.put(playerName, amount);
            player.sendMessage(ChatColor.GREEN + "Points set to " + amount + "!");
        }
    }

    // When player joins, set their points to their points
    public static void joinSet(Player player, int amount) {
        if (player != null) {
            String playerName = player.getName();
            points.put(playerName, amount);
        }
    }

    // remove the player's points
    public static void removePoints(Player player, int amount) {
        String playerName = player.getName();
        int currentPoints = getPoints(player);
        points.put(playerName, Math.max(0, currentPoints - amount));
        player.sendMessage(ChatColor.RED + "-" + amount + " points!");
    }

    // reset the player's points
    public static void resetPoints(Player player) {
        String playerName = player.getName();
        points.put(playerName, 0);
        player.sendMessage(ChatColor.GOLD + "Points reset!");
    }

    // set offline player's points
    public static void setOfflinePoints(String playerName, int amount) {
        points.put(playerName, amount);
        AchieveData.saveAchieveData();
    }

    // get offline player's points
    public static int getOfflinePoints(String playerName) {
        if (!points.containsKey(playerName)) {
            return 0;
        }
        return points.get(playerName);
    }

    // add offline player's points
    public static void addOfflinePoints(String playerName, int amount) {
        int currentPoints = getOfflinePoints(playerName);
        points.put(playerName, currentPoints + amount);
        AchieveData.saveAchieveData();
    }

    // remove offline player's points
    public static void removeOfflinePoints(String playerName, int amount) {
        int currentPoints = getOfflinePoints(playerName);
        points.put(playerName, Math.max(0, currentPoints - amount));
        AchieveData.saveAchieveData();
    }

    // reset offline player's points
    public static void resetOfflinePoints(String playerName) {
        points.put(playerName, 0);
        AchieveData.saveAchieveData();
    }

}