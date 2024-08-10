//package me.stryff.daxelnations.schedulers;
//
//import me.stryff.daxelnations.DaxelNations;
//import me.stryff.daxelnations.model.Grant;
//import net.luckperms.api.model.group.Group;
//import net.luckperms.api.model.user.User;
//import net.luckperms.api.node.types.InheritanceNode;
//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;
//
//import java.sql.SQLException;
//import java.util.List;
//import java.util.UUID;
//
//public class GrantUpdater implements Runnable {
//    private final DaxelNations plugin;
//    public GrantUpdater(DaxelNations plugin) {
//        this.plugin = plugin;
//    }
//    @Override
//    public void run() {
//        try {
//            List<Grant> grants = plugin.getDatabase().getGrantsByDurationNotLifetime();
//            if (grants != null) {
//                for (Grant grant : grants) {
//                    if (grant.isExpired()) {
//                        if (!grant.getStatus().equals("revoked")) {
//                            UUID uuid = UUID.fromString(grant.getPlayerUUID());
//                            Player p = Bukkit.getPlayer(uuid);
//                            User user = plugin.getLuckPerms().getUserManager().getUser(uuid);
//                            Group group = plugin.getLuckPerms().getGroupManager().getGroup(grant.getNewRank());
//                            plugin.getDatabase().expireGrant(grant.getPlayerUUID(), grant.getNewRank(), grant.getTimestamp());
//                            InheritanceNode newNode = InheritanceNode.builder(grant.getNewRank()).build();
//                            user.data().remove(newNode);
//                            p.sendMessage(plugin.colorCode("&cYou are no longer " + group.getDisplayName() + "&c!"));
//                        }
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//}
