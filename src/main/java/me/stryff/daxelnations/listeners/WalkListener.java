package me.stryff.daxelnations.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class WalkListener implements Listener {
    @EventHandler
    public void onWalk(PlayerMoveEvent e) {
        Player p = e.getPlayer();

    }
}
