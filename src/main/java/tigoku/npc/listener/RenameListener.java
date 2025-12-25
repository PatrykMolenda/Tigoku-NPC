package tigoku.npc.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import tigoku.npc.service.GuiFacade;
import tigoku.npc.service.NpcService;
import tigoku.npc.service.SessionService;
import tigoku.npc.util.Text;

public final class RenameListener implements Listener {
    private final SessionService sessions;
    private final NpcService npcService;
    private final Plugin plugin;
    private final GuiFacade gui;

    public RenameListener(SessionService sessions, NpcService npcService, Plugin plugin, GuiFacade gui) {
        this.sessions = sessions;
        this.npcService = npcService;
        this.plugin = plugin;
        this.gui = gui;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (!sessions.isRenaming(e.getPlayer())) return;
        String npcId = sessions.takeRename(e.getPlayer());
        if (npcId == null) return;

        String msg = e.getMessage();
        e.setCancelled(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                var opt = npcService.getDefinition(npcId);
                if (opt.isPresent()) {
                    var def = opt.get();
                    def.setDisplayName(msg);
                    npcService.putDefinition(def);
                    npcService.getSpawnedEntity(npcId).ifPresent(v -> npcService.applyDefinition(v, def));
                    gui.save();
                    e.getPlayer().sendMessage(Text.color("&aName changed to &f" + msg));
                }
            }
        }.runTask(plugin);
    }
}
