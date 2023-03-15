package io.github.cats1337;

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

        // get own points /points
        if (label.equalsIgnoreCase("points")) {
            if (args.length == 0) {
                // show the player their points
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    // Score points = AchievePoints.getPoints(player);
                    int points = AchievePoints.getPoints(player);
                    sender.sendMessage("You have " + points + " points!");
                } else {
                    sender.sendMessage("/points");
                }
            }
            // Get players points /points <player>
            else if (args.length == 1) {
                // Player player = AchievePoints.getPlayer(args[0]);
                Player player = (Player) sender;
                if (player != null) {
                    int points = AchievePoints.getPoints(player);
                    sender.sendMessage(player.getName() + " has " + points + " points!");
                } else {
                    sender.sendMessage("Player not found!");
                }
            }

            // ADMIN COMMANDS
            // Set points /points set <player> <points>
            else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
                Player cmdSender = (Player) sender;
                if (cmdSender.hasPermission("points.set")) {
                    // Player player = AchievePoints.getPlayer(args[2]);
                    Player player = (Player) sender;
                    int amount = Integer.parseInt(args[2]);
                    AchievePoints.setPoints(player, amount);
                    sender.sendMessage("Set " + player + "'s points to " + amount);
                    Main.LOGGER.info("Set " + player + "'s points to " + amount + " by " + sender.getName());
                }
                if (!cmdSender.hasPermission("points.set")) {
                    sender.sendMessage("You do not have permission to use this command!");
                } else {
                    sender.sendMessage("/points set <player> <points>");
                }
                // Add points /points add <player> <points>
                if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
                    if (cmdSender.hasPermission("points.add")) {
                        // Player player = AchievePoints.getPlayer(args[2]);
                        Player player = (Player) sender;
                        int amount = Integer.parseInt(args[2]);
                        AchievePoints.addPoints(player, amount);
                        sender.sendMessage("Added " + amount + " points to " + player);
                        Main.LOGGER.info("Added " + amount + " points to " + player + " by " + sender.getName());
                    }
                    if (!cmdSender.hasPermission("points.add")) {
                        sender.sendMessage("You do not have permission to use this command!");
                    } else {
                        sender.sendMessage("/points add <player> <points>");
                    }
                    // Remove points /points remove <player> <points>
                    if (args.length == 5 && args[0].equalsIgnoreCase("remove")) {
                        if (cmdSender.hasPermission("points.remove")) {
                            // Player player = AchievePoints.getPlayer(args[2]);
                            Player player = (Player) sender;
                            int amount = Integer.parseInt(args[3]);
                            AchievePoints.removePoints(player, amount);
                            sender.sendMessage("Removed " + amount + " points from " + player);
                            Main.LOGGER
                                    .info("Removed " + amount + " points from " + player + " by " + sender.getName());
                        }
                        if (!cmdSender.hasPermission("points.remove")) {
                            sender.sendMessage("You do not have permission to use this command!");
                        }
                    } else {
                        sender.sendMessage("/points remove <player> <points>");
                    }
                    // Reset points /points reset <player>
                    if (args.length == 5 && args[0].equalsIgnoreCase("reset")) {
                        if (cmdSender.hasPermission("points.reset")) {
                            // Player player = AchievePoints.getPlayer(args[2]);
                            Player player = (Player) sender;
                            AchievePoints.resetPoints(player);
                            sender.sendMessage("Reset " + player + "'s points");
                            Main.LOGGER.info("Reset " + player + "'s points by " + sender.getName());
                        }
                        if (!cmdSender.hasPermission("points.set")) {
                            sender.sendMessage("You do not have permission to use this command!");
                        } else {
                            sender.sendMessage("/points reset <player>");
                        }
                    }
                }
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