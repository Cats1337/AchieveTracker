package io.github.cats1337;

import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementListener implements Listener {
    
    @EventHandler
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        Advancement advancement = event.getAdvancement();
        Player player = event.getPlayer();
        String playerName = player.getName();
        String advancementName = advancement.getKey().getKey();
        org.bukkit.advancement.AdvancementDisplayType type = advancement.getDisplay().getType();
        String message = playerName + " just earned a " + type + " advancement: " + advancementName;
        
        System.out.println(message);
        player.sendMessage(message);

        if (type.equals("challenge")) {
            System.out.println("CHALLENGE");
            AchievePoints.addPoints(player, 15);
        }
        if (type.equals("goal")) {
            System.out.println("GOAL");
            AchievePoints.addPoints(player, 10);
        }
        if (type.equals("task")) {
            System.out.println("TASK");
            AchievePoints.addPoints(player, 5);
        }
    }

    static enum AdvancementDisplayType{
        TASK("task"),
        GOAL("goal"),
        CHALLENGE("challenge");

        private final String type;

        AdvancementDisplayType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}


    // public static AdvancementDisplayType getType(Advancement advancement, Player player) {
    //     AdvancementProgress progress = player.getAdvancementProgress(advancement);
    //     if (!progress.isDone()) {
    //         return null;
    //     }
    
    //     if (advancement.getKey().getKey().contains("challenge")) {
    //         return AdvancementDisplayType.CHALLENGE;
    //     }
    
    //     return null;
    //     // return type;
    // }
    


    //     AdvancementDisplayType getType(Advancement advancement) {
    //     AdvancementDisplayType type = AdvancementDisplayType.REGULAR;
    //     if (advancement.getKey().getKey().contains("challenge")) {
    //         type = AdvancementDisplayType.CHALLENGE;
    //     }
    //     if (advancement.getKey().getKey().contains("goal")) {
    //         type = AdvancementDisplayType.GOAL;
    //     }
    //     return type;
    // }