package io.github.cats1337;

import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementDisplayType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

@SuppressWarnings("deprecation")
public class AdvancementListener implements Listener {
    
    @EventHandler
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        Advancement advancement = event.getAdvancement();
        // if advancement is null, crafting advancement
        if (advancement == null || advancement.getDisplay() == null) {
            return;
        }
        Player player = event.getPlayer();

        org.bukkit.advancement.AdvancementDisplayType type = advancement.getDisplay().getType();

        if (type.equals(AdvancementDisplayType.CHALLENGE)) {
            // System.out.println("CHALLENGE");
            String title = "[" + advancement.getDisplay().getTitle() + "]";
            String description = advancement.getDisplay().getDescription().toString();

            // Create the hover event
            BaseComponent[] hoverEventComponents = new ComponentBuilder(description).create();

            // Create the final message
            BaseComponent[] messageComponents = new ComponentBuilder("You made the advancement ")
                    .color(ChatColor.WHITE)
                    .append(title)
                    .color(ChatColor.DARK_PURPLE)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverEventComponents))
                    .create();

            // Send the message to the player
            player.spigot().sendMessage(messageComponents);

            AchievePoints.addPoints(player, 15);
        }
        if (type.equals(AdvancementDisplayType.GOAL)) {
            // System.out.println("GOAL");
            String title = "[" + advancement.getDisplay().getTitle() + "]";
            String description = advancement.getDisplay().getDescription().toString();

            // Create the hover event
            BaseComponent[] hoverEventComponents = new ComponentBuilder(description).create();

            // Create the final message
            BaseComponent[] messageComponents = new ComponentBuilder("You made the advancement ")
                    .color(ChatColor.WHITE)
                    .append(title)
                    .color(ChatColor.DARK_AQUA)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverEventComponents))
                    .create();

            // Send the message to the player
            player.spigot().sendMessage(messageComponents);

            AchievePoints.addPoints(player, 10);
        }
        if (type.equals(AdvancementDisplayType.TASK)) {
            // System.out.println("TASK");
            String title = "[" + advancement.getDisplay().getTitle() + "]";
            String description = advancement.getDisplay().getDescription().toString();
            
            // Create the hover event
            BaseComponent[] hoverEventComponents = new ComponentBuilder(description).create();

            // Create the final message
            BaseComponent[] messageComponents = new ComponentBuilder("You made the advancement ")
                .color(ChatColor.WHITE)
                .append(title)
                .color(ChatColor.GREEN)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverEventComponents))
                .create();

            // Send the message to the player
            player.spigot().sendMessage(messageComponents);


            AchievePoints.addPoints(player, 5);

        }
        // if advancement is a crafting advancement ignore it
        if (advancement.getKey().getKey().contains("recipes")) {
            // System.out.println("CRAFTING");
            return;
        }
    }

}