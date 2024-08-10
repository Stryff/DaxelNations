package me.stryff.daxelnations.model;

import org.bukkit.Bukkit;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Grant {
    private int id;
    private String playerUUID;
    private String adminUUID;
    private String oldRank;
    private String newRank;
    private String timestamp;
    private String grantReason;
    private String grantDuration;
    private String status;

    public Grant(int id, String playerUUID, String adminUUID, String oldRank, String newRank, String timestamp, String grantReason, String grantDuration, String status) {
        this.id = id;
        this.playerUUID = playerUUID;
        this.adminUUID = adminUUID;
        this.oldRank = oldRank;
        this.newRank = newRank;
        this.timestamp = timestamp;
        this.grantReason = grantReason;
        this.grantDuration = grantDuration;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(String playerUUID) {
        this.playerUUID = playerUUID;
    }

    public String getAdminUUID() {
        return adminUUID;
    }

    public void setAdminUUID(String adminUUID) {
        this.adminUUID = adminUUID;
    }

    public String getOldRank() {
        return oldRank;
    }

    public void setOldRank(String oldRank) {
        this.oldRank = oldRank;
    }

    public String getNewRank() {
        return newRank;
    }

    public void setNewRank(String newRank) {
        this.newRank = newRank;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getGrantReason() {
        return grantReason;
    }

    public void setGrantReason(String grantReason) {
        this.grantReason = grantReason;
    }

    public String getGrantDuration() {
        return grantDuration;
    }

    public void setGrantDuration(String grantDuration) {
        this.grantDuration = grantDuration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Grant{" +
                "id=" + id +
                ", playerUUID='" + playerUUID + '\'' +
                ", adminUUID='" + adminUUID + '\'' +
                ", oldRank='" + oldRank + '\'' +
                ", newRank='" + newRank + '\'' +
                ", timestamp=" + timestamp +
                ", grantReason='" + grantReason + '\'' +
                ", grantDuration='" + grantDuration + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        long grantTime = Timestamp.valueOf(timestamp).getTime();
        long duration = parseDuration(grantDuration);
        return (currentTime - grantTime) > duration;
    }

    private long parseDuration(String duration) {
        if (duration == null || duration.isEmpty()) {
            return 0;
        }

        Pattern pattern = Pattern.compile("^(\\d+)([smhd])$");
        Matcher matcher = pattern.matcher(duration);

        if (matcher.matches()) {
            long value = Long.parseLong(matcher.group(1));
            char unit = matcher.group(2).charAt(0);

            switch (unit) {
                case 's':
                    return value * 1000;
                case 'm':
                    return value * 60 * 1000;
                case 'h':
                    return value * 60 * 60 * 1000;
                case 'd':
                    return value * 24 * 60 * 60 * 1000;
                default:
                    return 0;
            }
        } else {
            switch (duration) {
                case "Lifetime":
                    return Long.MAX_VALUE;
                case "10 Seconds":
                    return 10 * 1000;
                case "1 Day":
                    return 24 * 60 * 60 * 1000;
                case "7 Days":
                    return 7 * 24 * 60 * 60 * 1000;
                case "14 Days":
                    return 14 * 24 * 60 * 60 * 1000;
                case "30 Days":
                    return 30L * 24 * 60 * 60 * 1000;
                case "90 Days":
                    return 90L * 24 * 60 * 60 * 1000;
                case "180 Days":
                    return 180L * 24 * 60 * 60 * 1000;
                case "365 Days":
                    return 365L * 24 * 60 * 60 * 1000;
                default:
                    return 0;
            }
        }
    }
}
