package me.stryff.daxelnations.listeners;

import me.stryff.daxelnations.DaxelNations;
import me.stryff.daxelnations.model.PlayerNation;
import me.stryff.daxelnations.model.Players;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.sql.SQLException;

public class DeathListener implements Listener {
    private DaxelNations plugin = null;
    Location corner1;
    Location corner2;
    public DeathListener(DaxelNations plugin) {
        this.plugin = plugin;
        corner1 = plugin.getPvPCorner1();
        corner2 = plugin.getPvPCorner2();
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity().getKiller();
        if (p != null) { // Check if the killer is not null and is a Player
            PlayerNation nation;
            Players player;
            int points;
            int credits;
            if (isInCuboidRegion(p, corner1, corner2)) {
                if (plugin.getInvasionPlayers().contains(p)) {
                    points = 2;
                    credits = 15;
                } else {
                    points = 1;
                    credits = 10;
                }
                try {
                    nation = plugin.getDatabase().findPlayerNationByUUID(p.getUniqueId().toString());
                    if (nation != null) {
                        int curPower = nation.getPowerBalance();
                        int newPower = curPower + points;
                        nation.setPowerBalance(newPower);
                        plugin.getDatabase().updatePlayerNation(nation);
                    }
                    player = plugin.getDatabase().getPlayerByUUID(p.getUniqueId().toString());
                    if (player != null) {
                        double curBal = player.getCurrencyBalance();
                        double newBal = curBal + credits;
                        player.setCurrencyBalance(newBal);
                        plugin.getDatabase().updatePlayer(player);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    public boolean isInCuboidRegion(Player player, Location corner1, Location corner2) {
        Location playerLoc = player.getLocation();

        // Get the minimum and maximum coordinates for each axis
        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        // Check if player's location is within the cuboid region
        return playerLoc.getX() >= minX && playerLoc.getX() <= maxX &&
                playerLoc.getY() >= minY && playerLoc.getY() <= maxY &&
                playerLoc.getZ() >= minZ && playerLoc.getZ() <= maxZ;
    }
}
