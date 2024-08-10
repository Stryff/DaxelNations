package me.stryff.daxelnations.guis;

import me.stryff.daxelnations.db.Database;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NationGUI implements Listener {

    private final JavaPlugin plugin;
    private final Database database;
    private final FileConfiguration config;

    public NationGUI(JavaPlugin plugin, Database database) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.database = database;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 3 * 9, ChatColor.GRAY + "Choose a Nation");

        gui.setItem(10, createGlassPane("sylvora"));
        gui.setItem(12, createGlassPane("aerothorn"));
        gui.setItem(14, createGlassPane("equilonis"));
        gui.setItem(16, createGlassPane("lumatera"));

        player.openInventory(gui);
    }

    private ItemStack createGlassPane(String nation) {
        String materialName = config.getString("nations." + nation + ".display-material");
        assert materialName != null;
        Material material = Material.matchMaterial(materialName);
        assert material != null;
        ItemStack glassPane = new ItemStack(material);
        ItemMeta meta = glassPane.getItemMeta();
        if (meta != null) {
            String displayName = ChatColor.BOLD + config.getString("nations." + nation + ".display-name");
            String colorHex = config.getString("nations." + nation + ".color");
            ChatColor color = ChatColor.of("#" + colorHex);
            List<String> lore = plugin.getConfig().getStringList("nations." + nation + ".lore");
            List<String> coloredLore = new ArrayList<>();
            String name = nation.substring(0, 1).toUpperCase() + nation.substring(1);
            coloredLore.add(ChatColor.GRAY + "Click to join the nation of " + color + name + ChatColor.GRAY + "!");
            coloredLore.add(" ");
            for (String line : lore) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            coloredLore.add(" ");
            coloredLore.add(ChatColor.YELLOW + "Click to join");

            meta.setDisplayName(color + displayName);
            meta.setLore(coloredLore);

            glassPane.setItemMeta(meta);
        }
        return glassPane;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClick() == ClickType.LEFT) {
            Player player = (Player) event.getWhoClicked();
            if (event.getView().getTitle().equals(ChatColor.GRAY + "Choose a Nation")) {
                if (event.getRawSlot() >= event.getInventory().getSize()) {
                    return; // Clicked outside of inventory bounds
                }
                String nation = null;
                if (event.getRawSlot() == 10) {
                    nation = "sylvora";
                } else if (event.getRawSlot() == 12) {
                    nation = "aerothorn";
                } else if (event.getRawSlot() == 14) {
                    nation = "equilonis";
                } else if (event.getRawSlot() == 16) {
                    nation = "lumatera";
                } else {
                    event.setCancelled(true);
                    return; // Clicked on an empty slot
                }
                try {
                    joinedNation(player, nation);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                event.setCancelled(true);
            }
        }
    }




    private void joinedNation(Player p, String nation) throws SQLException {
        String colorHex = config.getString("nations." + nation + ".color");
        ChatColor color = ChatColor.of("#" + colorHex);
        String name = config.getString("nations." + nation + ".display-name");
        p.sendMessage(ChatColor.GREEN + "Successfully joined the nation of " + color + name + org.bukkit.ChatColor.GREEN + "!");
        p.playSound(p.getLocation(), "minecraft:entity.player.levelup", SoundCategory.AMBIENT, 3, 0.1f);
        PreparedStatement statement = database.getConnection()
                .prepareStatement("UPDATE player_nations SET nation = ?, role = ? WHERE uuid = ?");
        statement.setString(1, nation);
        statement.setString(2, "Newcomer");
        statement.setString(3, p.getUniqueId().toString());
        statement.executeUpdate();
        statement.close();
        p.closeInventory();
    }

}

