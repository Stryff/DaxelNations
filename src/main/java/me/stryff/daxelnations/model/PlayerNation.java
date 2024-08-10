package me.stryff.daxelnations.model;

public class PlayerNation {
    private String uuid;
    private String nation;
    private String role;
    private int power_balance;
    public PlayerNation(String uuid, String nation, String role, int power_balance) {
        this.uuid = uuid;
        this.nation = nation;
        this.role = role;
        this.power_balance = power_balance;
    }
    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public String getNation() {
        return nation;
    }
    public void setNation(String nation) {
        this.nation = nation;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public int getPowerBalance() {
        return power_balance;
    }
    public void setPowerBalance(int power_balance) { this.power_balance = power_balance; }
}
