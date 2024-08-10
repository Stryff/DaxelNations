package me.stryff.daxelnations.commands;

import me.stryff.daxelnations.DaxelNations;
import me.stryff.daxelnations.model.PlayerChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class ToggleChatCommand implements CommandExecutor {
    private final DaxelNations plugin;
    public ToggleChatCommand(DaxelNations plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to execute that command");
            return true;
        }
        Player p = (Player) sender;
        PlayerChat playerChat = null;
        try {
            playerChat = plugin.getDatabase().getPlayerChat(p.getUniqueId().toString());
            if (playerChat == null) {
                p.sendMessage(plugin.colorCode("&cA Database error has occcured! Please contact an administrator to fix this issue!"));
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        boolean toggled = playerChat.getToggled();
        if (!toggled) {
            playerChat.setToggled(true);
            try {
                plugin.getDatabase().updatePlayerChat(playerChat);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            p.sendMessage(plugin.colorCode("&b│ &7Successfully toggled your chat &aon&7."));
        } else {
            playerChat.setToggled(false);
            try {
                plugin.getDatabase().updatePlayerChat(playerChat);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            p.sendMessage(plugin.colorCode("&b│ &7Successfully toggled your chat &coff&7."));
        }
        return true;
    }
}
