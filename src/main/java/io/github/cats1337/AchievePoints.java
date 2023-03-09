package io.github.cats1337;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class AchievePoints {
    // points system
    // points are given for advancements

    // 5 points for regular "task" advancements
    // 10 points for goals
    // 15 points for challenges

    // store the points in a txt file

    // getPlayer
    static Player getPlayer(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        return player;
    }
    
    // give the player points
    static Object addPoints(Player player, int amount) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = scoreboard.getObjective("points");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("points", "dummy", ChatColor.GREEN + "Points");
        }
        Score score = objective.getScore(player.getName());
        score.setScore(score.getScore() + amount);
        player.sendMessage(ChatColor.GREEN + "+" + amount + " points!");
        return null;
    }

    // get the player's points
    static Score getPoints(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = scoreboard.getObjective("points");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("points", "dummy", ChatColor.GREEN + "Points");
        }
        Score score = objective.getScore(player.getName());
        player.sendMessage(score.getScore() + " points!");
        return score;
    }

    // set the player's points
    static Object setPoints(Player player, int amount) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = scoreboard.getObjective("points");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("points", "dummy", ChatColor.GREEN + "Points");
        }
        Score score = objective.getScore(player.getName());
        score.setScore(amount);
        player.sendMessage(ChatColor.GREEN + "Points set to " + amount + "!");
        return null;
    }

    // remove the player's points
    static Object removePoints(Player player, int amount) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = scoreboard.getObjective("points");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("points", "dummy", ChatColor.GREEN + "Points");
        }
        Score score = objective.getScore(player.getName());
        score.setScore(score.getScore() - amount);
        player.sendMessage(ChatColor.GREEN + "-" + amount + " points!");
        return null;
    }

    // reset the player's points
    static Object resetPoints(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = scoreboard.getObjective("points");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("points", "dummy", ChatColor.GREEN + "Points");
        }
        Score score = objective.getScore(player.getName());
        score.setScore(0);
        player.sendMessage(ChatColor.GREEN + "Points reset!");
        return null;
    }

}
