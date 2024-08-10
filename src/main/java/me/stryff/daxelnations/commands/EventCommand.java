package me.stryff.daxelnations.commands;

import me.stryff.daxelnations.DaxelNations;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EventCommand implements CommandExecutor {
    private final DaxelNations plugin;

    public EventCommand(DaxelNations plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        String usage = ChatColor.YELLOW + "Usage: " + ChatColor.WHITE + "/event <start|stop|menu> <event>";
        String noPerms = ChatColor.RED + "You do not have permission to execute that command!";

        if (!sender.hasPermission("daxel.admin")) {
            sender.sendMessage(noPerms);
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(usage);
            return true;
        }

        if (args[0].equals("start")) {
            if (args.length < 2) {
                sender.sendMessage(usage);
                return true;
            }
            if (plugin.eventIsActive) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&8[&c&lDAXEL EVENTS&8] &cThe event " + plugin.activeEvent + " &cis already active!"));
                return true;
            }
            if (args[1].equals("NATION_INVASION")) {
                plugin.getInvasion().startInvasion();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&8[&c&lDAXEL EVENTS&8] &fSuccessfully started &aNation Invasion &fevent!"));
            } else if (args[1].equals("WILD_WEST")) {
                plugin.getWildWest().startEvent();
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&8[&c&lDAXEL EVENTS&8] &fSuccessfully started &6Wild West &fevent!"));
            }
        } else if (args[0].equals("stop") || args[0].equals("end")) {
            if (!plugin.eventIsActive) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&8[&c&lDAXEL EVENTS&8] &cThere are no events active!"));
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(usage);
                return true;
            }

            if (args[1].equals("NATION_INVASION")) {
                if (plugin.activeEvent.equals("&aNation Invasion")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&8[&c&lDAXEL EVENTS&8] &fSuccessfully ended &aNation Invasion &fevent!"));
                    plugin.getInvasion().endInvasion();
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&8[&c&lDAXEL EVENTS&8] &cThe event &aNation Invasion &cis not active!"));
                }
            } else if (args[1].equals("WILD_WEST")) {
                if (plugin.activeEvent.equals("&6Wild West")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&8[&c&lDAXEL EVENTS&8] &fSuccessfully ended &6Wild West &fevent!"));
                    plugin.getWildWest().endEvent();
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&8[&c&lDAXEL EVENTS&8] &cThe event &6Wild West &cis not active!"));
                }
            } else {
                // End all events
                if (plugin.activeEvent != null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&8[&c&lDAXEL EVENTS&8] &fSuccessfully ended &c" + plugin.activeEvent + " &fevent!"));
                    plugin.getInvasion().endInvasion();
                    plugin.getWildWest().endEvent();
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&8[&c&lDAXEL EVENTS&8] &cThere are no events active!"));
                }
            }
        } else if (args[0].equals("menu")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&8[&c&lDAXEL EVENTS&8] &fThis menu is coming soon!"));
            return true;
        }

        return true;
    }
}
