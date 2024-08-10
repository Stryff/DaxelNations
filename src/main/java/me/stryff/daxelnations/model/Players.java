package me.stryff.daxelnations.model;

public class Players {
    private String uuid;
    private String userName;
    private String displayName;
    private String rank;
    private String firstJoined;
    private String lastSeen;
    private long playtime;
    private String ipAddress;
    private double currencyBalance;
    private boolean bannedStatus;
    private boolean mutedStatus;
    private int warnCount;

    public Players(String uuid, String userName, String displayName, String rank, String firstJoined, String lastSeen, long playtime, String ipAddress, double currencyBalance, boolean bannedStatus, boolean mutedStatus, int warnCount) {
        this.uuid = uuid;
        this.userName = userName;
        this.displayName = displayName;
        this.rank = rank;
        this.firstJoined = firstJoined;
        this.lastSeen = lastSeen;
        this.playtime = playtime;
        this.ipAddress = ipAddress;
        this.currencyBalance = currencyBalance;
        this.bannedStatus = bannedStatus;
        this.mutedStatus = mutedStatus;
        this.warnCount = warnCount;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getFirstJoined() {
        return firstJoined;
    }

    public void setFirstJoined(String firstJoined) {
        this.firstJoined = firstJoined;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public long getPlaytime() {
        return playtime;
    }

    public void setPlaytime(long playtime) {
        this.playtime = playtime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public double getCurrencyBalance() {
        return currencyBalance;
    }

    public void setCurrencyBalance(double currencyBalance) {
        this.currencyBalance = currencyBalance;
    }

    public boolean isBannedStatus() {
        return bannedStatus;
    }

    public void setBannedStatus(boolean bannedStatus) {
        this.bannedStatus = bannedStatus;
    }

    public boolean isMutedStatus() {
        return mutedStatus;
    }

    public void setMutedStatus(boolean mutedStatus) {
        this.mutedStatus = mutedStatus;
    }

    public int getWarnCount() {
        return warnCount;
    }

    public void setWarnCount(int warnCount) {
        this.warnCount = warnCount;
    }
}

