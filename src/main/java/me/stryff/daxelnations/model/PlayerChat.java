package me.stryff.daxelnations.model;

public class PlayerChat {
    private String uuid;
    private String tag;
    private String chatColor;
    private boolean toggled;

    public PlayerChat(String uuid, String tag, String chatColor, boolean toggled) {
        this.uuid = uuid;
        this.tag = tag;
        this.chatColor = chatColor;
        this.toggled = toggled;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getChatColor() {
        return chatColor;
    }

    public void setChatColor(String chatColor) {
        this.chatColor = chatColor;
    }
    public boolean getToggled() {
        return toggled;
    }
    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

}

