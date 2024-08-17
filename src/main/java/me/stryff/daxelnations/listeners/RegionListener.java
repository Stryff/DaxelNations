package me.stryff.daxelnations.listeners;

import me.stryff.daxelnations.DaxelNations;
import me.stryff.daxelnations.model.Players;
import net.raidstone.wgevents.events.RegionEnteredEvent;
import net.raidstone.wgevents.events.RegionLeftEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RegionListener implements Listener {
    private final DaxelNations plugin;
    private final Map<Player, Block> lastBlocks = new HashMap<>();  // Track last block under the player

    public RegionListener(DaxelNations plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRegionEnter(RegionEnteredEvent event) {
        Player p = event.getPlayer();
        try {
            Players player = plugin.getDatabase().getPlayerByUUID(p.getUniqueId().toString());
            if (player != null) {
                player.setLocation(event.getRegionName().toLowerCase());
                plugin.getDatabase().updatePlayer(player);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @EventHandler
    public void onRegionLeave(RegionLeftEvent event) {
        Player p = event.getPlayer();
        try {
            Players player = plugin.getDatabase().getPlayerByUUID(p.getUniqueId().toString());
            if (player != null) {
                if (event.getRegionName().contains("pad")) {
                    player.setLocation("spawn");
                    plugin.getDatabase().updatePlayer(player);
                } else if (event.getRegionName().contains("spawn")) {
                    if (p.isFlying()) {
                        p.sendMessage(DaxelNations.colorCode("&cPlease exit flight before entering FFA!"));
                        event.setCancelled(true);
                        pushPlayerBackToSpawn(p);
                        return;
                    }
                    if (p.getWalkSpeed() > 1) {
                        p.sendMessage(DaxelNations.colorCode("&cPlease reset walk speed before entering FFA!"));
                        event.setCancelled(true);
                        pushPlayerBackToSpawn(p);
                        return;
                    }
                    p.setAllowFlight(false);
                    player.setFlightStatus(false);

                    player.setLocation("ffa");
                    plugin.getDatabase().updatePlayer(player);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location loc = e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation();
        Block b = loc.getBlock();

        // Get the last block the player was on
        Block lastBlock = lastBlocks.get(p);

        if (lastBlock != null && lastBlock.equals(b)) {
            // Player is still on the same block, do nothing
            return;
        }

        try {
            Players player = plugin.getDatabase().getPlayerByUUID(p.getUniqueId().toString());
            if (player != null && player.getLocation() != null) {
                switch (player.getLocation().toLowerCase()) {
                    case "pad1":
                        if (b.getType() == Material.SLIME_BLOCK) {
                            Vector velocity = new Vector(1.0d, 1.5d, 0.0d).multiply(2);
                            if (player.getLocation().equalsIgnoreCase("pad1")) {
                                velocity.setY(0.5d);  // Adjust for pad1
                            }
                            p.setVelocity(velocity);
                            p.playSound(p, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 3, 2);
                        }
                        break;
                    case "pad2":
                        if (b.getType() == Material.SLIME_BLOCK) {
                            Vector velocity = new Vector(10.0d, 1.715d, 0.0d);
                            p.setVelocity(velocity);
                            p.playSound(p, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 3, 2);
                        }
                        break;
                    case "pad3":
                        if (b.getType() == Material.SLIME_BLOCK) {
                            Vector velocity = new Vector(10.0d, 1.75d, 0.0d);
                            p.setVelocity(velocity);
                            p.playSound(p, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 3, 2);
                        }
                        break;
                    case "pad4":
                        if (b.getType() == Material.SLIME_BLOCK) {
                            Vector velocity = new Vector(0.0d, 1.75d, 10.0d);
                            p.setVelocity(velocity);
                            p.playSound(p, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 3, 2);
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        // Update the last block the player was on
        lastBlocks.put(p, b);
    }

    private void pushPlayerBackToSpawn(Player p) {
        // Get the spawn world and the (0, 0) location in that world
        World spawnWorld = Bukkit.getWorld("spawn");
        if (spawnWorld != null) {
            Location spawnLocation = new Location(spawnWorld, 0, p.getLocation().getY(), 0);

            // Calculate direction vector towards the spawn location
            Vector direction = spawnLocation.toVector().subtract(p.getLocation().toVector()).normalize();

            // Push the player back in that direction
            p.setVelocity(direction.multiply(1.5));  // Adjust the multiplier for push strength
        }
    }
}
