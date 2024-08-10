package me.stryff.daxelnations.commands;

import me.stryff.daxelnations.DaxelNations;
import me.stryff.daxelnations.model.Players;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class InfoCommand implements CommandExecutor {
    private final DaxelNations plugin;

    public InfoCommand(DaxelNations plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /info <player>");
            return true;
        }

        Player p = (sender instanceof Player) ? (Player) sender : null;
        String playerName = args[0];
        Player target = Bukkit.getPlayer(playerName);

        if (target == null || !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "The player " + playerName + " is not online!");
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "Retrieving info on " + ChatColor.GOLD + playerName);
        sender.sendMessage(" ");

        try {
            Players player = plugin.getDatabase().getPlayerByUUID(target.getUniqueId().toString());
            if (player != null) {
                sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.AQUA + "UUID: " + player.getUuid());
                sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.AQUA + "Username: " + player.getUserName());
                sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.AQUA + "Display Name: " + player.getDisplayName());
                sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.AQUA + "Rank: " + player.getRank());
                sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.AQUA + "First Login: " + player.getFirstJoined());
                sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.AQUA + "Last Login: " + player.getLastSeen());
                sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.AQUA + "Play Time: " + formatPlayTime(player.getPlaytime()));
                sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.AQUA + "IP: " + player.getIpAddress());
                sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.AQUA + "Balance: " + player.getCurrencyBalance());
                sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.AQUA + "Banned Status: " + player.isBannedStatus());
                sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.AQUA + "Muted Status: " + player.isMutedStatus());
                sender.sendMessage(ChatColor.GRAY + "- " + ChatColor.AQUA + "Warn Count: " + player.getWarnCount());
            } else {
                sender.sendMessage(ChatColor.RED + "Failed to retrieve player information for " + playerName);
            }
        } catch (SQLException e) {
            sender.sendMessage(ChatColor.RED + "An error occurred while retrieving player information.");
            e.printStackTrace();
        }

        return true;
    }

    private String formatPlayTime(long seconds) {
        long days = TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) % 24;
        long minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60;
        long remainingSeconds = seconds % 60;

        StringBuilder formattedTime = new StringBuilder();

        if (days > 0) {
            formattedTime.append(days).append("d, ");
        }
        if (hours > 0) {
            formattedTime.append(hours).append("h, ");
        }
        if (minutes > 0) {
            formattedTime.append(minutes).append("m, ");
        }
        if (remainingSeconds > 0 || formattedTime.length() == 0) {
            formattedTime.append(remainingSeconds).append("s");
        } else {
            // Remove the trailing comma and space
            formattedTime.setLength(formattedTime.length() - 2);
        }

        return formattedTime.toString();
    }

}
