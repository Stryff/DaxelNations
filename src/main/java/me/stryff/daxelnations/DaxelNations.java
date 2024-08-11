package me.stryff.daxelnations;

import me.stryff.daxelnations.commands.*;
import me.stryff.daxelnations.completers.BalanceCompleter;
import me.stryff.daxelnations.completers.EventCompleter;
import me.stryff.daxelnations.db.Database;
import me.stryff.daxelnations.guis.NationGUI;
import me.stryff.daxelnations.listeners.*;
import me.stryff.daxelnations.schedulers.*;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DaxelNations extends JavaPlugin {
    private Database database;
    private NationGUI nationGUI;
    private NationInvasion invasion;
    private WildWestEvent wildWest;
    private final List<Player> invasionPlayers = new CopyOnWriteArrayList<>();
    private LuckPerms api;
    private final Location corner1 = new Location(Bukkit.getWorld("world"), 166, 87, -1455);
    private final Location corner2 = new Location(Bukkit.getWorld("world"), 108, 71, -1506);
    private List<String> scoreboardLayout;
    public boolean eventIsActive;
    public String activeEvent;
    public String eventInfo1;
    public String eventInfo2;
    private BukkitTask grantUpdaterTask;

    @Override
    public void onEnable() {
        try {
            this.database = new Database(this);
            database.initializeDatabase();
        } catch (SQLException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[DaxelNations] Error connecting and creating MySQL tables!");
            e.printStackTrace();
        }
        this.wildWest = new WildWestEvent(this);
        this.invasion = new NationInvasion(this, database);
        this.nationGUI = new NationGUI(this, database);

        getServer().getPluginManager().registerEvents(new FirstJoinListener(this, nationGUI), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new EnterRegionListener(), this);

        getCommand("nations").setExecutor(new NationCommand(nationGUI, this));
        getCommand("playerinfo").setExecutor(new InfoCommand(this));
        getCommand("nick").setExecutor(new NickCommand(this));
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("balance").setTabCompleter(new BalanceCompleter());
        getCommand("event").setExecutor(new EventCommand(this));
        getCommand("event").setTabCompleter(new EventCompleter());
        getCommand("nbt").setExecutor(new NbtCommand());
        getCommand("togglechat").setExecutor(new ToggleChatCommand(this));

        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[DaxelNations] LOOOOOOOOL!");

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            api = provider.getProvider();
            new LuckPermsListeners(this);
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[DaxelNations] LuckPerms not found!");
        }

        saveDefaultConfig();
        eventIsActive = false;
        activeEvent = "&cNone";
        eventInfo1 = "&cNone";
        eventInfo2 = "&cNone";
        scoreboardLayout = getConfig().getStringList("scoreboard.layout");

        Bukkit.getScheduler().runTask(this, () -> {
            ScoreboardUpdater scoreboardUpdater = new ScoreboardUpdater(this, scoreboardLayout);
            scoreboardUpdater.start();
        });
        new PlaytimeScheduler(this, database);
    }

    @Override
    public void onDisable() {
        getDatabase().closeConnection();
        if (grantUpdaterTask != null) {
            grantUpdaterTask.cancel();
        }
    }

    public Database getDatabase() {
        return database;
    }
    public NationInvasion getInvasion() {
        return invasion;
    }
    public WildWestEvent getWildWest() {
        return wildWest;
    }

    public List<Player> getInvasionPlayers() {
        return invasionPlayers;
    }

    public LuckPerms getLuckPerms() {
        return api;
    }

    public Location getPvPCorner1() {
        return corner1;
    }

    public Location getPvPCorner2() {
        return corner2;
    }
    public String colorCode(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
