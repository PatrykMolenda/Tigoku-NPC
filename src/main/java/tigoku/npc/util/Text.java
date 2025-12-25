package tigoku.npc.util;

import org.bukkit.ChatColor;

public final class Text {
    private Text() {}

    public static String color(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}

