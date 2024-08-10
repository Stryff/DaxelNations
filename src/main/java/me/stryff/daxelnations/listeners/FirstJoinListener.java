package me.stryff.daxelnations.listeners;

import me.stryff.daxelnations.DaxelNations;
import me.stryff.daxelnations.guis.NationGUI;
import me.stryff.daxelnations.model.PlayerChat;
import me.stryff.daxelnations.model.PlayerNation;
import me.stryff.daxelnations.model.Players;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FirstJoinListener implements Listener {
    private final DaxelNations plugin;
    private final NationGUI gui;
    public FirstJoinListener(DaxelNations plugin, NationGUI gui) {
        this.plugin = plugin;
        this.gui = gui;
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        try {
            long currentTimeMillis = System.currentTimeMillis();
            Date currentDate = new Date(currentTimeMillis);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedLastSeen = sdf.format(currentDate);

            Players player = plugin.getDatabase().getPlayerByUUID(p.getUniqueId().toString());
            if (player != null) {
                player.setLastSeen(formattedLastSeen);
                plugin.getDatabase().updatePlayer(player);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    @EventHandler
    public void onFirstJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (p.hasPlayedBefore()) {
            try {
                PlayerNation playerNation = this.plugin.getDatabase().findPlayerNationByUUID(p.getUniqueId().toString());
                if (playerNation == null) {
                    playerNation = new PlayerNation(p.getUniqueId().toString(), "None", "None", 0);
                    this.plugin.getDatabase().createPlayerNation(playerNation);
                    gui.openGUI(p);
                }

                PlayerChat playerChat = this.plugin.getDatabase().getPlayerChat(p.getUniqueId().toString());
                if (playerChat == null) {
                    playerChat = new PlayerChat(p.getUniqueId().toString(), "", "", true);
                    this.plugin.getDatabase().createPlayerChat(playerChat);
                }

                long firstPlayedTimestamp = p.getFirstPlayed();
                Date firstPlayedDate = new Date(firstPlayedTimestamp);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedFirstPlayed = sdf.format(firstPlayedDate);
                String address = String.valueOf(p.getAddress()).replace("/", "");

                // Create the Players object with the formatted first joined date
                Players players = new Players(p.getUniqueId().toString(), p.getName(),
                        p.getName(),
                        "default",
                        formattedFirstPlayed,
                        formattedFirstPlayed,
                        0,
                        address,
                        0.0,
                        false,
                        false,
                        0
                );
                this.plugin.getDatabase().createPlayer(players);
                gui.openGUI(p);
            } catch (SQLException er) {
                er.printStackTrace();
            }
        }
        try {
            Players player = plugin.getDatabase().getPlayerByUUID(p.getUniqueId().toString());
            boolean isBanned = player.isBannedStatus();
            if (isBanned) {
                p.kickPlayer("You are banned from this server!");
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + p.getName() + " attempted to login while banned!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
