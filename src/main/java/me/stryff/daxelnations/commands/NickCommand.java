package me.stryff.daxelnations.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.stryff.daxelnations.DaxelNations;
import me.stryff.daxelnations.model.Players;
import xyz.haoshoku.nick.api.NickAPI;

import java.sql.SQLException;

public class NickCommand implements CommandExecutor {
    private final DaxelNations plugin;

    public NickCommand(DaxelNations plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute that command!");
            return true;
        }

        Player p = (Player) sender;

        // Check if args array has at least one element
        if (args.length == 0) {
            p.sendMessage(ChatColor.RED + "You need to specify a nickname!");
            return true;
        }

        try {
            Players players = plugin.getDatabase().getPlayerByUUID(p.getUniqueId().toString());
            if (players != null) {
                players.setUserName(args[0]);
                plugin.getDatabase().updatePlayer(players);
            }
        } catch (SQLException e) {
            p.sendMessage(ChatColor.RED + "An error occurred while updating your nickname in the database.");
            e.printStackTrace();
            return true;
        }

        p.sendMessage(ChatColor.DARK_GREEN + "Successfully set the nickname to " + ChatColor.YELLOW + args[0]);
        return true;
    }

}
