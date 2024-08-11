package me.stryff.daxelnations.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Random;

public class EnterRegionListener implements Listener {

    // Method to randomly teleport a player within the world "smp"
    public void randomlyTeleportPlayer(Player player) {
        World world = Bukkit.getWorld("smp");
        Random random = new Random();
        Location randomLocation;

        do {
            // Generate random coordinates within the 250000x250000 border
            double x = random.nextDouble() * 250000 * (random.nextBoolean() ? 1 : -1);
            double z = random.nextDouble() * 250000 * (random.nextBoolean() ? 1 : -1);

            // Get the highest block at the (x, z) location
            int highestY = world.getHighestBlockYAt((int) x, (int) z);
            randomLocation = new Location(world, x, highestY, z);

            // Check if the block is solid and not water or lava
        } while (isUnsafeLocation(randomLocation));

        // Teleport the player to the safe random location
        player.teleport(randomLocation);
    }

    // Method to check if a location is unsafe
    private boolean isUnsafeLocation(Location location) {
        Material blockType = location.getBlock().getType();
        Material blockBelowType = location.clone().subtract(0, 1, 0).getBlock().getType();

        // Check if the location is in the air, water, lava, or an unsafe block type
        return location.getY() < 0 || location.getY() > location.getWorld().getMaxHeight() ||
                blockType == Material.WATER || blockType == Material.LAVA ||
                blockBelowType == Material.AIR || blockBelowType == Material.WATER ||
                blockBelowType == Material.LAVA;
    }

    @EventHandler
    public void onRegion(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        Location loc = p.getLocation();
        Location loc1 = new Location(Bukkit.getWorld("Lobby"), 52, -3, -8);
        Location loc2 = new Location(Bukkit.getWorld("Lobby"), 35, -3, 8);
        double minX = Math.min(loc1.getX(), loc2.getX());
        double maxX = Math.max(loc1.getX(), loc2.getX());
        double minY = Math.min(loc1.getY(), loc2.getY());
        double maxY = Math.max(loc1.getY(), loc2.getY());
        double minZ = Math.min(loc1.getZ(), loc2.getZ());
        double maxZ = Math.max(loc1.getZ(), loc2.getZ());
        if (loc.getX() >= minX && loc.getX() <= maxX && loc.getY() >= minY &&
                loc.getY() <= maxY && loc.getZ() >= minZ && loc.getZ() <= maxZ) {
            // Call the teleport method here
            randomlyTeleportPlayer(p);
        }
    }
}
