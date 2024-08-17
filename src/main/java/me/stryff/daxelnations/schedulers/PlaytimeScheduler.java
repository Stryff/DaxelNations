package me.stryff.daxelnations.schedulers;

import me.stryff.daxelnations.DaxelNations;
import me.stryff.daxelnations.db.Database;
import me.stryff.daxelnations.model.Players;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlaytimeScheduler {

    private final DaxelNations plugin;
    private final Database database;
    private final Map<UUID, BukkitRunnable> playerTasks = new HashMap<>();

    public PlaytimeScheduler(DaxelNations plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
    }

    public void startTracking(Player player) {
        UUID uuid = player.getUniqueId();
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                increasePlayerPlaytime(player);
            }
        };
        task.runTaskTimer(plugin, 20L, 20L); // Run every second
        playerTasks.put(uuid, task);
    }

    public void stopTracking(Player player) {
        UUID uuid = player.getUniqueId();
        BukkitRunnable task = playerTasks.remove(uuid);
        if (task != null) {
            task.cancel();
            // Ensure the player's playtime is updated in the database when they log off
            updatePlayerPlaytime(player);
        }
    }

    private void increasePlayerPlaytime(Player player) {
        String uuid = player.getUniqueId().toString();
        Players players;
        try {
            players = database.getPlayerByUUID(uuid);
            if (players != null) {
                long currentPlaytime = players.getPlaytime();
                currentPlaytime++; // Increment playtime by one second
                players.setPlaytime(currentPlaytime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePlayerPlaytime(Player player) {
        String uuid = player.getUniqueId().toString();
        Players players;
        try {
            players = database.getPlayerByUUID(uuid);
            if (players != null) {
                database.updatePlayer(players);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
