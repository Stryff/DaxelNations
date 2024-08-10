package me.stryff.daxelnations.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class DaxelNationsTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (sender instanceof Player) {
            if (args.length == 1) {
                for (Player player : ((Player) sender).getWorld().getPlayers()) {
                    completions.add(player.getName());
                }
            }
        }

        return completions;
    }
}
