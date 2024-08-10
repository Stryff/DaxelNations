package me.stryff.daxelnations.commands;

import me.stryff.daxelnations.DaxelNations;
import me.stryff.daxelnations.guis.NationGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class NationCommand implements CommandExecutor {
    private final NationGUI gui;
    private final DaxelNations plugin;
    public NationCommand(NationGUI gui, DaxelNations plugin) {
        this.gui = gui;
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to execute that command!");
            return false;
        }
        Player p = (Player) sender;
        try {
            String nation = this.plugin.getDatabase().stringNationByUUID(p.getUniqueId().toString());
            if (nation == null || nation.equals("None")) {
                gui.openGUI(p);
            } else {
                String colorHex = plugin.getConfig().getString("nations." + nation + ".color");
                net.md_5.bungee.api.ChatColor color = net.md_5.bungee.api.ChatColor.of("#" + colorHex);
                String name = plugin.getConfig().getString("nations." + nation + ".display-name");
                p.sendMessage(ChatColor.RED + "You are already in the nation of " + color + name + ChatColor.RED + "!");
            }
        } catch (SQLException er) {
            er.printStackTrace();
        }
        return true;
    }

}
