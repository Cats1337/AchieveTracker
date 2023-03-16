package io.github.cats1337;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AchieveCommands implements CommandExecutor {
    /*
     * admin commands: /points set <player> <points> - set the points of a player
     * admin commands: /points add <player> <points> - add points to a player
     * admin commands: /points remove <player> <points> - remove points from a
     * player
     * admin commands: /points reset <player> - reset the points of a player
     * admin commands: /points reload - reload the config
     * 
     * admin commands: /points hologram - create a hologram with the leaderboard
     * admin commands: /points hologram delete - delete the hologram with the
     * leaderboard
     * admin commands: /points hologram reload - reload the hologram with the
     * leaderboard
     * admin commands: /points help - show the help menu
     */

    /*
     * config options:
     * hologram location
     * hologram update interval
     * hologram lines - default: Title + 10 places, with 1 place per line, 1 line
     * per player, +1 line showing players place (if place 15 show lines 1-10, then
     * line 15. <player>), +1 line for the page number / next page / previous page
     */

    /*
     * permissions:
     * points.set
     * points.add
     * points.remove
     * points.reset
     * points.reload
     * points.hologram
     * points.hologram.delete
     * points.hologram.reload
     * points.help
     */

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // if console sends command, tell it they can't and need to be a player
        if(!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use points commands!");
            return true;
        }
        // get own points /points
        if (label.equalsIgnoreCase("points")) {
            if (args.length == 0) {
                // show the player their points
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    int points = AchievePoints.getPoints(player);
                    sender.sendMessage(ChatColor.GRAY + "You have " + ChatColor.BOLD + ChatColor.AQUA + points + ChatColor.GRAY + " points!");
                }
            } else if (args.length == 1) {
                Player player = Bukkit.getPlayer(args[0]);
                if (player != null) {
                    int points = AchievePoints.getPoints(player);
                    sender.sendMessage(player.getName() + " has " + points + " points!");
                    sender.sendMessage(ChatColor.DARK_AQUA + player.getName() + ChatColor.GRAY +  " has " + ChatColor.BOLD + ChatColor.AQUA + points + ChatColor.GRAY + " points!");
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "Player not found!");
                }
            } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
                Player cmdSender = (Player) sender;
                if (cmdSender.hasPermission("points.set")) {
                    Player player = Bukkit.getPlayer(args[1]);
                    int amount = Integer.parseInt(args[2]);
                    AchievePoints.setPoints(player, amount);
                    sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.BOLD + player.getName() + ChatColor.YELLOW + "'s points to " + ChatColor.BOLD + amount + ChatColor.YELLOW + ".");
                    Main.LOGGER.info("Set " + player.getName() + "'s points to " + amount + " by " + sender.getName());
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                }
            } else if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
                Player cmdSender = (Player) sender;
                if (cmdSender.hasPermission("points.add")) {
                    Player player = Bukkit.getPlayer(args[1]);
                    int amount = Integer.parseInt(args[2]);
                    AchievePoints.addPoints(player, amount);
                    sender.sendMessage(ChatColor.GREEN + "Added " + ChatColor.BOLD + amount + ChatColor.GREEN + " points to " + ChatColor.BOLD + player.getName() + ChatColor.GREEN + ".");
                    Main.LOGGER.info("Added " + amount + " points to " + player.getName() + " by " + sender.getName());
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command!");
                }
            } else if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
                Player cmdSender = (Player) sender;
                if (cmdSender.hasPermission("points.remove")) {
                    Player player = Bukkit.getPlayer(args[1]);
                    int amount = Integer.parseInt(args[2]);
                    AchievePoints.removePoints(player, amount);
                    sender.sendMessage(ChatColor.RED + "Removed " + ChatColor.BOLD + amount + ChatColor.RED + " points from " + ChatColor.BOLD + player.getName());
                    Main.LOGGER.info("Removed " + amount + " points from " + player.getName() + " by " + sender.getName());
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command!");
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
                Player cmdSender = (Player) sender;
                if (cmdSender.hasPermission("points.reset")) {
                    Player player = Bukkit.getPlayer(args[1]);
                    AchievePoints.resetPoints(player);
                    sender.sendMessage("Reset " + player.getName() + "'s points");
                    sender.sendMessage(ChatColor.GOLD + "Reset " + ChatColor.BOLD + player.getName() + ChatColor.GOLD + "'s points.");
                    Main.LOGGER.info("Reset " + player.getName() + "'s points by " + sender.getName());
                } else {
                    sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use this command!");
                }
            } else {
                sender.sendMessage("/points reset <player>");
            }
        }

        return false;
    }
}

/*
 * admin commands: /points set <player> <points> - set the points of a player
 * admin commands: /points add <player> <points> - add points to a player
 * admin commands: /points remove <player> <points> - remove points from a
 * player
 * admin commands: /points reset <player> - reset the points of a player
 * 
 * admin commands: /points hologram - create a hologram with the leaderboard
 * admin commands: /points hologram delete - delete the hologram with the
 * leaderboard
 * admin commands: /points hologram reload - reload the hologram with the
 * leaderboard
 * admin commands: /points help - show the help menu
 */