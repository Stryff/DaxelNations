package me.stryff.daxelnations.schedulers;

import me.stryff.daxelnations.DaxelNations;
import me.stryff.daxelnations.model.Players;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WildWestEvent {

    private final DaxelNations plugin;
    private final List<Player> pvpZonePlayers;
    private final Random random;

    public WildWestEvent(DaxelNations plugin) {
        this.plugin = plugin;
        this.pvpZonePlayers = new ArrayList<>();
        this.random = new Random();
        scheduleEvent();
    }

    private void scheduleEvent() {
        new BukkitRunnable() {
            @Override
            public void run() {
                startEvent();
            }
        }.runTaskTimer(plugin, 0, 20 * 60 * 10); // Run every 10 minutes
    }
    double bountyAmount;
    public void startEvent() {
        // Clear previous event data
        if (!plugin.eventIsActive) {
            pvpZonePlayers.clear();

            // Find players in PvP zone and add them to the list
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (isInCuboidRegion(player, plugin.getPvPCorner1(), plugin.getPvPCorner2())) {
                    pvpZonePlayers.add(player);
                }
            }
            if (!pvpZonePlayers.isEmpty()) {
                // Select a random player from the PvP zone
                Player targetPlayer = pvpZonePlayers.get(random.nextInt(pvpZonePlayers.size()));
                Players p = null;
                try {
                    p = plugin.getDatabase().getPlayerByUUID(targetPlayer.getUniqueId().toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (p != null) {
                    double bountyPercentage = (p.getPlaytime() < 5 * 3600) ? 0.025 : 0.05;
                    applyBounty(targetPlayer, bountyPercentage);
                    giveNotchApple(targetPlayer);
                }
                User user = plugin.getLuckPerms().getPlayerAdapter(Player.class).getUser(targetPlayer);
                String suffix = ChatColor.translateAlternateColorCodes('&', user.getCachedData().getMetaData().getSuffix());
                plugin.activeEvent = "&6Wild West";
                plugin.eventIsActive = true;
                plugin.eventInfo1 = ChatColor.translateAlternateColorCodes('&', suffix + targetPlayer.getName());
                plugin.eventInfo2 = ChatColor.translateAlternateColorCodes('&', "&6" + bountyAmount);
            }
        }
    }

    public void endEvent() {
        plugin.activeEvent = "&cNone";
        plugin.eventIsActive = false;
        plugin.eventInfo1 = "&cNone";
        plugin.getInvasionPlayers().clear();
    }
    private void applyBounty(Player targetPlayer, double bountyPercentage) {
        Players players = null;
        Player killer = targetPlayer.getKiller();
        Players pkiller = null;
        double playerCoins = 0;
        try {
            players = plugin.getDatabase().getPlayerByUUID(targetPlayer.getUniqueId().toString());
            if (killer != null) {
                pkiller = plugin.getDatabase().getPlayerByUUID(killer.getUniqueId().toString());
            }
            playerCoins = players.getCurrencyBalance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        bountyAmount = playerCoins * bountyPercentage;

        if (killer != null) {
            if (players != null && pkiller != null) {
                players.setCurrencyBalance(players.getCurrencyBalance() - bountyAmount);
                pkiller.setCurrencyBalance(pkiller.getCurrencyBalance() + bountyAmount);
            }
        }
    }

    private void giveNotchApple(Player player) {
        ItemStack notchApple = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
        player.getInventory().addItem(notchApple);
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