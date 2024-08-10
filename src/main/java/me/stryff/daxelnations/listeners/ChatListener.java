package me.stryff.daxelnations.listeners;

import me.stryff.daxelnations.DaxelNations;
import me.stryff.daxelnations.model.PlayerChat;
import me.stryff.daxelnations.model.Players;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.SQLException;

public class ChatListener implements Listener {
    private final DaxelNations plugin;

    public ChatListener(DaxelNations plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        e.setCancelled(true);
        if (p.hasMetadata("grant_data")) {
            e.setCancelled(true);
            Bukkit.broadcastMessage("lol");
            return;
        }
        User user = plugin.getLuckPerms().getPlayerAdapter(Player.class).getUser(p);
        boolean muted = false;
        String tag = "";
        String chatColor = "";
        try {
            Players player = plugin.getDatabase().getPlayerByUUID(p.getUniqueId().toString());
            if (player != null) {
                muted = player.isMutedStatus();
            }
            PlayerChat playerChat = plugin.getDatabase().getPlayerChat(p.getUniqueId().toString());
            if (playerChat != null) {
                chatColor = playerChat.getChatColor();
                tag = playerChat.getTag();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (!muted) {
            String prefix = user.getCachedData().getMetaData().getPrefix();
            if (prefix == null) prefix = "";
            else prefix = ChatColor.translateAlternateColorCodes('&', prefix);

            String suffix = user.getCachedData().getMetaData().getSuffix();
            if (suffix == null) suffix = "";
            else suffix = ChatColor.translateAlternateColorCodes('&', suffix);

            String pg = user.getPrimaryGroup();
            String rank = plugin.getLuckPerms().getGroupManager().getGroup(pg).getDisplayName();
            if (rank == null) rank = "";
            else rank = ChatColor.translateAlternateColorCodes('&', rank);

            StringBuilder hoverText = new StringBuilder();
            hoverText.append(ChatColor.GRAY).append("Name: ").append(suffix).append(p.getName()).append("\n");
            hoverText.append(ChatColor.GRAY).append("Rank: ").append(suffix).append(rank).append("\n");
            hoverText.append(ChatColor.GRAY).append("Tag: ").append(tag.isEmpty() ? "None" : tag);

            String nameAndRank = prefix + suffix + p.getName();
            String messageBeforeColon = nameAndRank + ChatColor.WHITE + ": ";
            String messageAfterColon = chatColor == null ? e.getMessage() : ChatColor.translateAlternateColorCodes('&', chatColor) + e.getMessage();

            PlayerChat pC = null;
            try {
                pC = plugin.getDatabase().getPlayerChat(p.getUniqueId().toString());
                if (pC == null) {
                    p.sendMessage(plugin.colorCode("&cA Database error has occurred! Please contact an administrator to fix this issue!"));
                    return;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            if (!pC.getToggled()) {
                p.sendMessage(plugin.colorCode("&cYour chat is currently toggled off. Use /togglechat to toggle it on!"));
                e.setCancelled(true);
                return;
            }
            Bukkit.getServer().getConsoleSender().sendMessage(messageBeforeColon + messageAfterColon);
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerChat ppC = null;
                try {
                    ppC = plugin.getDatabase().getPlayerChat(player.getUniqueId().toString());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                // Null check for ppC
                if (ppC != null && ppC.getToggled()) {
                    player.spigot().sendMessage(ChatMessageType.CHAT, new ComponentBuilder(messageBeforeColon)
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText.toString()).create()))
                            .append(messageAfterColon)
                            .reset()  // Ensure the message after the colon doesn't carry the hover event
                            .create());
                }
            }
        }
        e.setCancelled(true);
    }

}
