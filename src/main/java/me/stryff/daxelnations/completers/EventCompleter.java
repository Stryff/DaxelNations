package me.stryff.daxelnations.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventCompleter implements TabCompleter {

    private final List<String> subcommands = Arrays.asList("start", "stop", "menu");
    private final List<String> modmodes = Arrays.asList("toggle", "invsee");
    private final List<String> events = Arrays.asList("NATION_INVASION", "WILD_WEST");

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            // Suggest subcommands
            for (String subcommand : subcommands) {
                if (subcommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    suggestions.add(subcommand);
                }
            }
        } else if (args.length == 2) {
            // Suggest events for "start" and "stop" subcommands
            if (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("stop")) {
                for (String event : events) {
                    if (event.toLowerCase().startsWith(args[1].toLowerCase())) {
                        suggestions.add(event);
                    }
                }
            }
        }

        return suggestions;
    }
}
