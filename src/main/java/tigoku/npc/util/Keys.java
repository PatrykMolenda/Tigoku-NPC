package tigoku.npc.util;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public final class Keys {
    private Keys() {}

    public static NamespacedKey npcId(Plugin plugin) {
        return new NamespacedKey(plugin, "npc_id");
    }
}

