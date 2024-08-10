package me.stryff.daxelnations.schedulers;

import me.stryff.daxelnations.DaxelNations;
import me.stryff.daxelnations.db.Database;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class NationInvasion {

    private final DaxelNations plugin;
    private final Database database;
    private final List<String> nations;

    public NationInvasion(DaxelNations plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
        this.nations = new ArrayList<>();
        nations.add("sylvora");
        nations.add("aerothorn");
        nations.add("equilonis");
        nations.add("lumatera");
        scheduleEvent();
    }

    private void scheduleEvent() {
        new BukkitRunnable() {
            @Override
            public void run() {
                startInvasion();
            }
        }.runTaskTimer(plugin, 20, 20 * 60 * 10); // Run every 10 minutes (20 ticks * 60 seconds * 10 minutes)
    }

    public void startInvasion() {
        if (!plugin.eventIsActive) {
            Random random = new Random();
            String selectedNation = nations.get(random.nextInt(nations.size()));
            getPlayersFromNation(selectedNation);
            Bukkit.broadcastMessage(ChatColor.RED + "NATION INVASION! The nation " + selectedNation + " has been selected!");

            // Update activeEvent
            plugin.activeEvent = "&aNation Invasion";
            plugin.eventIsActive = true;
            plugin.eventInfo1 = getNationDisplay(selectedNation);

            Bukkit.getScheduler().runTaskLater(plugin, this::endInvasion, 20 * 60 * 2);
        }
    }

    private void getPlayersFromNation(String nation) {
        try {
            PreparedStatement statement = database.getConnection().prepareStatement("SELECT uuid FROM player_nations WHERE nation = ?");
            statement.setString(1, nation);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                Player player = Bukkit.getPlayer(UUID.fromString(uuid));
                if (player != null && player.isOnline()) {
                    plugin.getInvasionPlayers().add(player);
                }
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void endInvasion() {
        plugin.activeEvent = "&cNone";
        plugin.eventIsActive = false;
        plugin.eventInfo1 = "&cNone";
        plugin.getInvasionPlayers().clear();
    }

    public String getNationDisplay(String nation) {
        String colorHex = plugin.getConfig().getString("nations." + nation + ".color");
        if (colorHex == null) {
            colorHex = "FFFFFF"; // Default to white if no color is found
        }
        ChatColor color;
        try {
            color = ChatColor.of("#" + colorHex);
        } catch (IllegalArgumentException e) {
            color = ChatColor.WHITE; // Fallback to white if the colorHex is invalid
        }
        return color + plugin.getConfig().getString("nations." + nation + ".display-name");
    }
}
