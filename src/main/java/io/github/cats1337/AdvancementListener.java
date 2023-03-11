package io.github.cats1337;

import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementDisplayType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementListener implements Listener {
    
    @EventHandler
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        Advancement advancement = event.getAdvancement();
        Player player = event.getPlayer();
        org.bukkit.advancement.AdvancementDisplayType type = advancement.getDisplay().getType();

        if (type.equals(AdvancementDisplayType.CHALLENGE)) {
            System.out.println("CHALLENGE");
            AchievePoints.addPoints(player, 15);
        }
        if (type.equals(AdvancementDisplayType.GOAL)) {
            System.out.println("GOAL");
            AchievePoints.addPoints(player, 10);
        }
        if (type.equals(AdvancementDisplayType.TASK)) {
            System.out.println("TASK");
            AchievePoints.addPoints(player, 5);
        }
    }

}