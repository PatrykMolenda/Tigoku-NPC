package tigoku.npc.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import tigoku.npc.Permissions;
import tigoku.npc.service.GuiFacade;
import tigoku.npc.service.NpcService;

public final class NpcEntityListener implements Listener {
    private final NpcService npcService;
    private final GuiFacade gui;

    public NpcEntityListener(NpcService npcService, GuiFacade gui) {
        this.npcService = npcService;
        this.gui = gui;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractAtEntityEvent e) {
        Entity entity = e.getRightClicked();
        if (!(entity instanceof Villager villager)) return;

        var idOpt = npcService.getNpcId(entity);
        if (idOpt.isEmpty()) return;

        Player p = e.getPlayer();

        if (p.hasPermission(Permissions.EDIT) || p.hasPermission(Permissions.ADMIN)) {
            if (p.isSneaking()) {
                e.setCancelled(true);
                gui.openTradeList(p, idOpt.get());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e) {
        if (!npcService.getNpcId(e.getEntity()).isPresent()) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDeathEvent e) {
        if (!npcService.getNpcId(e.getEntity()).isPresent()) return;
        e.getDrops().clear();
        e.setDroppedExp(0);
    }
}
