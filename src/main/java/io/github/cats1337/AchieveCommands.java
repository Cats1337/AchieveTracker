package io.github.cats1337;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class AchieveCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // if console sends command, tell it they can't and need to be a player
        if (!(sender instanceof Player)) {
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
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You have &b" + points + " &7points!"));
                }
            } 

        // help /points help
        else if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            helpMenu(sender);
        }
        else if (args.length == 1 && args[0].equalsIgnoreCase("set")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cIncorrect usage: /points set <player> <amount>"));
        }
        else if (args.length == 1 && args[0].equalsIgnoreCase("add")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cIncorrect usage: /points add <player> <amount>"));
        }
        else if (args.length == 1 && args[0].equalsIgnoreCase("remove")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cIncorrect usage: /points remove <player> <amount>"));
        }
        else if (args.length == 1 && args[0].equalsIgnoreCase("reset")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cIncorrect usage: /points reset <player>"));
        }

        // set points /points set <player> <amount>
         else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            Player cmdSender = (Player) sender;
            if (cmdSender.hasPermission("points.set")) {
                    Player player = Bukkit.getPlayer(args[1]);
                    int amount = Integer.parseInt(args[2]);
                    // set the player's points if they are online
                    if (player != null) {
                        AchievePoints.setPoints(player, amount);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSet &b" + player.getName() + "&7's points to &e" + amount + " &7!"));

                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Your points have been &eset &7to &e" + amount + " &7!"));
                    } 
                    // set the offline player's points, if they exist in the database
                    if (player == null && AchieveData.playerCheck(args[1]) != null) {
                        String offlinePlayer = AchieveData.playerCheck(args[1]);
                        String[] offlinePlayerData = offlinePlayer.split(",");
                        String offlinePlayerName = offlinePlayerData[0];
                        AchievePoints.setOfflinePoints(offlinePlayerName, amount);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSet &b" + offlinePlayerName + "&7's points to &e" + amount + " &7!"));
                    }
                    else if(player == null && AchieveData.playerCheck(args[1]) == null) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Player not found!"));
                    }
                    AchieveData.saveData();
                }
                else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cIncorrect usage: /points set <player> <amount>"));
                } if (!cmdSender.hasPermission("points.set")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to use this command!"));
            }
            return true;
        }

        // add points /points add <player> <amount>
        else if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            Player cmdSender = (Player) sender;
            if (cmdSender.hasPermission("points.add")) {
                    Player player = Bukkit.getPlayer(args[1]);
                    int amount = Integer.parseInt(args[2]);
                    // add points to the player if they are online
                    if (player != null) {
                        AchievePoints.addPoints(player, amount);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aAdded &b" + amount + " &7points to &b" + player.getName() + " &7!"));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You have been given &a+" + amount + " &7points!"));
                        AchieveMain.LOGGER.info(sender + " added " + amount + " points to " + player.getName() + "!");
                    } 
                    // add points to the offline player if they exist in the database
                    if (player == null && AchieveData.playerCheck(args[1]) != null) {
                        String offlinePlayer = AchieveData.playerCheck(args[1]);
                        String[] offlinePlayerData = offlinePlayer.split(",");
                        String offlinePlayerName = offlinePlayerData[0];
                        int offlinePlayerPoints = Integer.parseInt(offlinePlayerData[1]);
                        AchievePoints.addOfflinePoints(offlinePlayer, offlinePlayerPoints + amount);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aAdded &b" + amount + " &7points to &b" + offlinePlayerName + " &7!"));
                        AchieveMain.LOGGER.info(sender + " added " + amount + " points to " + offlinePlayerName + "!");
                    }
                    
                    else if (player == null && AchieveData.playerCheck(args[1]) == null) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Player not found!"));
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cIncorrect usage: /points add <player> <amount>"));
            } if (!cmdSender.hasPermission("points.add")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to use this command!"));
            }
            return true;
        }

        // remove points /points remove <player> <amount>
        else if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
            Player cmdSender = (Player) sender;
            if (cmdSender.hasPermission("points.remove")) {
                    Player player = Bukkit.getPlayer(args[1]);
                    int amount = Integer.parseInt(args[2]);
                    // remove points from the player if they are online
                    if (player != null) {
                        AchievePoints.removePoints(player, amount);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eRemoved &b" + amount + " &7points from &b" + player.getName() + " &7!"));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You have lost &e-" + amount + " &7points!"));
                        AchieveMain.LOGGER.info(sender + " removed " + amount + " points from " + player.getName() + "!");
                    } 
                    // remove points from the offline player if they exist in the database
                    if (player == null && AchieveData.playerCheck(args[1]) != null) {
                        String offlinePlayer = AchieveData.playerCheck(args[1]);
                        String[] offlinePlayerData = offlinePlayer.split(",");
                        String offlinePlayerName = offlinePlayerData[0];
                        int offlinePlayerPoints = Integer.parseInt(offlinePlayerData[1]);
                        AchievePoints.removeOfflinePoints(offlinePlayer, offlinePlayerPoints - amount);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eRemoved &b" + amount + " &7points from &b" + offlinePlayerName + " &7!"));
                        AchieveMain.LOGGER.info(sender + " removed " + amount + " points from " + offlinePlayerName + "!");
                    }
                    
                    else if (player == null && AchieveData.playerCheck(args[1]) == null) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Player not found!"));
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',  "&cIncorrect usage: /points remove <player> <amount>"));
            }  if (!cmdSender.hasPermission("points.remove")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to use this command!"));
            }
            return true;
        }

        // reset points /points reset <player>
        else if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            Player cmdSender = (Player) sender;
            if (cmdSender.hasPermission("points.reset")) {
                    Player player = Bukkit.getPlayer(args[1]);
                    // reset the player's points if they are online
                    if (player != null) {
                        AchievePoints.resetPoints(player);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eReset &b" + player.getName() + " &7's points!"));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Your points have been reset!"));
                        AchieveMain.LOGGER.info(sender + " reset " + player.getName() + "'s points!");
                    } 
                    // reset the offline player's points if they exist in the database
                    if (player == null && AchieveData.playerCheck(args[1]) != null) {
                        String offlinePlayer = AchieveData.playerCheck(args[1]);
                        String[] offlinePlayerData = offlinePlayer.split(",");
                        String offlinePlayerName = offlinePlayerData[0];
                        AchievePoints.resetOfflinePoints(offlinePlayer);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eReset &b" + offlinePlayerName + " &7's points!"));
                        AchieveMain.LOGGER.info(sender + " reset " + offlinePlayerName + "'s points!");
                    }
                    
                    else if (player == null && AchieveData.playerCheck(args[1]) == null) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Player not found!"));
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cIncorrect usage: /points reset <player>"));
                } if (!cmdSender.hasPermission("points.reset")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to use this command!"));
            }
            return true;
            }
            
            // Hologram Reload /points hologram reload
            else if (args.length == 2 && args[0].equalsIgnoreCase("hologram") && args[1].equalsIgnoreCase("reload")) {
                Player cmdSender = (Player) sender;
                if (cmdSender.hasPermission("points.hologram.reload")) {
                    AchieveHolograms.updateHologram();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Hologram Reloaded!"));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to use this command!"));
                }
                return true;
            }
            // Hologram Move /points hologram move <x,y,z>
            else if (args.length == 5 && args[0].equalsIgnoreCase("hologram") && args[1].equalsIgnoreCase("move")) {
                Player cmdSender = (Player) sender;
                if (cmdSender.hasPermission("points.hologram.move")) { 
                    Double x = Double.parseDouble(args[2]);
                    Double y = Double.parseDouble(args[3]);
                    Double z = Double.parseDouble(args[4]);
                    Location newLoc = new Location(Bukkit.getWorld("world"), x, y, z);
                    AchieveHolograms.hologram.setPosition(newLoc);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Hologram moved!"));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to use this command!"));
                }
                return true;
            }
            // Hologram MoveHere /points hologram movehere
            else if (args.length == 2 && args[0].equalsIgnoreCase("hologram") && args[1].equalsIgnoreCase("movehere")) {
                Player cmdSender = (Player) sender;
                if (cmdSender.hasPermission("points.hologram.movehere")) {
                    Double x = cmdSender.getLocation().getX();
                    Double y = cmdSender.getLocation().getY() + 3;
                    Double z = cmdSender.getLocation().getZ();
                    Location newLoc = new Location(Bukkit.getWorld("world"), x, y, z);
                    AchieveHolograms.hologram.setPosition(newLoc);
                    
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Hologram moved!"));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to use this command!"));
                }
                return true;
            }
            // Hologram align /points hologram align
            else if (args.length == 2 && args[0].equalsIgnoreCase("hologram") && args[1].equalsIgnoreCase("align")) {
                Player cmdSender = (Player) sender;
                if (cmdSender.hasPermission("points.hologram.align")) {
                    AchieveHolograms.alignHologram();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Hologram aligned!"));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to use this command!"));
                }
                return true;
            }
            else if (args.length == 1 && args[0].equalsIgnoreCase("leaderboard") || args[0].equalsIgnoreCase("lb")) {
                Player cmdSender = (Player) sender;
                if (cmdSender.hasPermission("points.leaderboard")) {
                    ArrayList<String> leaderboard = AchieveData.getLeaderboard(cmdSender);
                    for (int i = 0; i < leaderboard.size(); i++) {
                        // print each line of the leaderboard
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', leaderboard.get(i)));
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to use this command!"));
                }
                return true;
            }

            // if the player is online, get their points
            else if (args.length == 1 && !args[0].equalsIgnoreCase("help") && !args[0].equalsIgnoreCase("set")
                    && !args[0].equalsIgnoreCase("add") && !args[0].equalsIgnoreCase("remove")
                    && !args[0].equalsIgnoreCase("reset") && !args[0].equalsIgnoreCase("leaderboard")
                    && !args[0].equalsIgnoreCase("lb")) {
                Player player = Bukkit.getPlayer(args[0]);
                if (player != null) {
                    int points = AchievePoints.getPoints(player);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&b" + player.getName() + " &7has &b" + points + " &7points!"));
                }
                // if player is not found, check if the player is in the achieves.yml file by using playerCheck()
                if (player == null && AchieveData.playerCheck(args[0]) != null) {

                    String offlinePlayer = AchieveData.playerCheck(args[0]);
                    String[] offlinePlayerData = offlinePlayer.split(",");
                    String offlinePlayerName = offlinePlayerData[0];
                    int offlinePlayerPoints = Integer.parseInt(offlinePlayerData[1]);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&3" + offlinePlayerName + " &7has &b" + offlinePlayerPoints + " &7points!"));
                } 

                // if not a player, and not a command, and not a player in the achieves.yml file, then the player is not found
                else if (args.length == 1 && player == null && AchieveData.playerCheck(args[0]) == null && !args[0].equalsIgnoreCase("help") && !args[0].equalsIgnoreCase("set") && !args[0].equalsIgnoreCase("add") && !args[0].equalsIgnoreCase("remove") && !args[0].equalsIgnoreCase("reset") && !args[0].equalsIgnoreCase("leaderboard") && !args[0].equalsIgnoreCase("lb")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Player not found!"));
                }
            }
            else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid Command: run /points help"));
            }
        }
        return true;
    }
    
    // help menu
    public static void helpMenu(CommandSender sender) {
        Player cmdSender = (Player) sender;
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&l---- &b&lPoints Help&3&l ----"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b /points &7 - &fcheck your points"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b /points <player> &7 - &fcheck anothers player's points"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b /points <leaderboard, lb> &7 - &fshows the leaderboard"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b /points <leaderboard, lb> <page> &7 - &fshows specified leaderboard page"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b /points help &7 - &fshows help menu, duh?"));


        if (cmdSender.hasPermission("points.set")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b /points set <player> <points> &7 - &fset the points of a player"));
        }
        if (cmdSender.hasPermission("points.add")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b /points add <player> <points> &7 - &fadd points to a player"));
        }
        if (cmdSender.hasPermission("points.remove")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b /points remove <player> <points> &7 - &fremove points from a player"));
        }
        if (cmdSender.hasPermission("points.reset")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b /points reset <player> &7 - &freset the points of a player"));
        }
        if (cmdSender.hasPermission("points.hologram")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b /points hologram move <x> <y> <z> &7 - &fmove the hologram to specified coordinates"));
                    
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b /points hologram movehere &7 - &fmove the hologram to your location"));

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b /points hologram align &7 - &falign the hologram to the nearest block center"));

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b /points hologram reload &7 - &freload the hologram"));
        }
    }
}