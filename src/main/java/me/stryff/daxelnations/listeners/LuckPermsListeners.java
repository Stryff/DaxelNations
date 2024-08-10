package me.stryff.daxelnations.listeners;

import me.stryff.daxelnations.DaxelNations;
import me.stryff.daxelnations.model.Players;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.SQLException;
import java.sql.Timestamp;

public class LuckPermsListeners {
    private final DaxelNations plugin;

    public LuckPermsListeners(DaxelNations plugin) {
        this.plugin = plugin;
        if (plugin.getLuckPerms() != null) {
            EventBus eventBus = plugin.getLuckPerms().getEventBus();
            eventBus.subscribe(this.plugin, UserDataRecalculateEvent.class, this::onGroupChange);
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[DaxelNations] Failed to initialize LuckPermsListeners: LuckPerms API is null!");
        }
    }

    private void onGroupChange(UserDataRecalculateEvent e) {
        User p = e.getUser();

        try {
            Players player = plugin.getDatabase().getPlayerByUUID(p.getUniqueId().toString());
            if (player != null) {
                String oldRank = player.getRank();
                String newRank = e.getData().getMetaData().getPrimaryGroup();

                if (!oldRank.equals(newRank)) {
                    player.setRank(newRank);
                    plugin.getDatabase().updatePlayer(player);

                    // Log the promotion
                    logPromotion(null, player.getUuid(), oldRank, newRank);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void logPromotion(String adminUUID, String playerUUID, String oldRank, String newRank) {
    }
}
