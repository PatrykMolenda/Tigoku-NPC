package tigoku.npc.service;

import org.bukkit.entity.Player;
import tigoku.npc.gui.GuiSession;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class SessionService {
    private final Map<UUID, GuiSession> sessions = new HashMap<>();
    private final Set<UUID> switching = new HashSet<>();
    private final Map<UUID, String> pendingRename = new HashMap<>();

    public void set(Player player, GuiSession session) {
        UUID uuid = player.getUniqueId();
        switching.add(uuid);
        sessions.put(uuid, session);
    }

    public GuiSession get(Player player) {
        return sessions.get(player.getUniqueId());
    }

    public void clear(Player player) {
        UUID uuid = player.getUniqueId();
        if (switching.remove(uuid)) {
            return;
        }
        sessions.remove(uuid);
    }

    public void clearAll() {
        sessions.clear();
        switching.clear();
        pendingRename.clear();
    }

    public void startRename(Player player, String npcId) {
        pendingRename.put(player.getUniqueId(), npcId);
    }

    public String takeRename(Player player) {
        return pendingRename.remove(player.getUniqueId());
    }

    public boolean isRenaming(Player player) {
        return pendingRename.containsKey(player.getUniqueId());
    }
}
