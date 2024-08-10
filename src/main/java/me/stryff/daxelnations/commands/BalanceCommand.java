package me.stryff.daxelnations.commands;

import me.stryff.daxelnations.DaxelNations;
import me.stryff.daxelnations.model.Players;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class BalanceCommand implements CommandExecutor {
    private final DaxelNations plugin;
    public BalanceCommand(DaxelNations plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        String usage = ChatColor.GREEN + "Usage: " + ChatColor.WHITE + "/balance <set|add|take|reset> <player> [amount]";
        String noPerms = ChatColor.RED + "You do not have permission to execute that command!";
        if (!sender.hasPermission("daxel.admin")) {
            sender.sendMessage(noPerms);
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(usage);
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        String targetNotOnline = ChatColor.RED + "The player " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " is not found!";
        String sqlError = ChatColor.RED + "The player data from " + args[1] + " could not be retrieved!";
        if (target == null) {
            sender.sendMessage(targetNotOnline);
            return true;
        }
        Players player = null;
        String resetSuccess = ChatColor.GREEN + "Successfully reset balance of " + ChatColor.DARK_GREEN + target.getName();
        try {
            player = plugin.getDatabase().getPlayerByUUID(target.getUniqueId().toString());
            if (player == null) {
                sender.sendMessage(sqlError);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (args.length == 2) {
            if (!args[0].equals("reset")) {
                sender.sendMessage(usage);
                return true;
            }
            try {
                player.setCurrencyBalance(0);
                plugin.getDatabase().updatePlayer(player);
                sender.sendMessage(resetSuccess);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (args.length > 2) {
            double amount = Double.parseDouble(args[2]);
            String takeSuccess = ChatColor.GREEN + "Successfully removed " + amount + " from balance of " + ChatColor.DARK_GREEN + target.getName();
            String addSuccess = ChatColor.GREEN + "Successfully added " + amount + " to balance of " + ChatColor.DARK_GREEN + target.getName();
            String setSuccess = ChatColor.GREEN + "Successfully set balance of " + ChatColor.DARK_GREEN + target.getName() + ChatColor.GREEN + " to " + amount;
            if (args[0].equals("take")) {
                try {
                    double balance = player.getCurrencyBalance();
                    player.setCurrencyBalance(balance - amount);
                    plugin.getDatabase().updatePlayer(player);
                    sender.sendMessage(takeSuccess);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (args[0].equals("add")) {
                try {
                    double balance = player.getCurrencyBalance();
                    player.setCurrencyBalance(balance + amount);
                    plugin.getDatabase().updatePlayer(player);
                    sender.sendMessage(addSuccess);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (args[0].equals("set")) {
                try {
                    player.setCurrencyBalance(amount);
                    plugin.getDatabase().updatePlayer(player);
                    sender.sendMessage(setSuccess);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }
}
