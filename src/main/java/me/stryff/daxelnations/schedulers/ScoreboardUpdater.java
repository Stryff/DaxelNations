package me.stryff.daxelnations.schedulers;

import me.stryff.daxelnations.DaxelNations;
import me.stryff.daxelnations.model.Players;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScoreboardUpdater extends BukkitRunnable {
    private final DaxelNations plugin;
    private final List<String> scoreboardLayout;
    private final boolean usePlaceholderAPI;
    private final Map<Player, Scoreboard> playerScoreboards;

    public ScoreboardUpdater(DaxelNations plugin, List<String> scoreboardLayout) {
        this.plugin = plugin;
        this.scoreboardLayout = scoreboardLayout;
        this.usePlaceholderAPI = plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
        this.playerScoreboards = new HashMap<>();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateScoreboard(player);
        }
    }

    public void start() {
        this.runTaskTimer(plugin, 0L, 20L); // Run the task every second (20 ticks)
    }

    private void updateScoreboard(Player p) {
        Scoreboard scoreboard = playerScoreboards.getOrDefault(p, Bukkit.getScoreboardManager().getNewScoreboard());
        Players player = null;
        try {
            player = plugin.getDatabase().getPlayerByUUID(p.getUniqueId().toString());
            if (player == null) {
                player = new Players(null, null, null, null, null, null, 0, null, 0, false, false, 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Clear existing objectives
        scoreboard.getObjectives().forEach(Objective::unregister);

        Objective objective = scoreboard.registerNewObjective("MinigameCore", "dummy", ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("scoreboard.title")));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Set<String> addedLines = new HashSet<>();
        int score = scoreboardLayout.size() - 1; // Start from the bottom of the list

        for (String line : scoreboardLayout) {
            line = ChatColor.translateAlternateColorCodes('&', line);
            if (line.contains("%event%")) {
                if (plugin.activeEvent == null) {
                    plugin.activeEvent = "&cNone";
                }
                line = line.replace("%event%", ChatColor.translateAlternateColorCodes('&', plugin.activeEvent));

                // Add the event line
                if (!addedLines.contains(line)) {
                    objective.getScore(line).setScore(score);
                    addedLines.add(line);
                    score--;
                }

                // If the active event is a nation invasion, add a line below with the active nation
                if ("&aNation Invasion".equals(plugin.activeEvent) && plugin.eventInfo1 != null) {
                    String nationLine = ChatColor.translateAlternateColorCodes('&', " &7- &fNation: " + plugin.eventInfo1);
                    if (!addedLines.contains(nationLine)) {
                        objective.getScore(nationLine).setScore(score);
                        addedLines.add(nationLine);
                        score--;
                    }
                }

                // If the active event is Wild West, add two lines below with the event info
                if ("&6Wild West".equals(plugin.activeEvent)) {
                    if (plugin.eventInfo1 != null) {
                        String eventInfoLine1 = ChatColor.translateAlternateColorCodes('&', " &7- &fPlayer: " + plugin.eventInfo1);
                        if (!addedLines.contains(eventInfoLine1)) {
                            objective.getScore(eventInfoLine1).setScore(score);
                            addedLines.add(eventInfoLine1);
                            score--;
                        }
                    }
                    if (plugin.eventInfo2 != null) {
                        String eventInfoLine2 = ChatColor.translateAlternateColorCodes('&', " &7- &fBounty: " + plugin.eventInfo2);
                        if (!addedLines.contains(eventInfoLine2)) {
                            objective.getScore(eventInfoLine2).setScore(score);
                            addedLines.add(eventInfoLine2);
                            score--;
                        }
                    }
                }
            } else {
                if (line.contains("%money%")) {
                    line = line.replace("%money%", String.valueOf(player.getCurrencyBalance()));
                }
                if (usePlaceholderAPI) {
                    line = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(p, line);
                }

                // Check for duplicates and ensure lines are added
                if (!addedLines.contains(line) || line.contains("%money%")) {
                    objective.getScore(line).setScore(score);
                    addedLines.add(line);
                    score--;
                }
            }
        }

        // Remove old lines that are not in the new layout to prevent flickering
        for (String entry : scoreboard.getEntries()) {
            if (!addedLines.contains(entry)) {
                scoreboard.resetScores(entry);
            }
        }

        p.setScoreboard(scoreboard);
        playerScoreboards.put(p, scoreboard);
    }
}
