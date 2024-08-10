package me.stryff.daxelnations.schedulers;

import me.stryff.daxelnations.DaxelNations;
import me.stryff.daxelnations.db.Database;
import me.stryff.daxelnations.model.Players;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class PlaytimeScheduler {

    private final DaxelNations plugin;
    private final Database database;

    public PlaytimeScheduler(DaxelNations plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
        startScheduler();
    }

    private void startScheduler() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    increasePlayerPlaytime(player);
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // Run every second
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
                database.updatePlayer(players);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
