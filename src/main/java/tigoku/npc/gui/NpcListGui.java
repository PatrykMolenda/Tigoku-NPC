package tigoku.npc.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import tigoku.npc.model.NpcDefinition;
import tigoku.npc.service.NpcService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class NpcListGui {
    public static final String TITLE = "NPC: List";

    private final NpcService npcService;
    private final GuiItemFactory items;

    public NpcListGui(NpcService npcService, GuiItemFactory items) {
        this.npcService = npcService;
        this.items = items;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(player, 54, TITLE);

        List<NpcDefinition> defs = new ArrayList<>(npcService.getDefinitions().values());
        defs.sort(Comparator.comparing(NpcDefinition::getId));

        int slot = 0;
        for (NpcDefinition def : defs) {
            if (slot >= 45) break;
            inv.setItem(slot++, items.button(
                    Material.VILLAGER_SPAWN_EGG,
                    "&e" + def.getId(),
                    List.of(
                            "&7Name: &f" + (def.getDisplayName() == null ? "-" : def.getDisplayName()),
                            "&7Left-click: &fEdit trades",
                            "&7Right-click: &fTeleport"
                    )
            ));
        }

        inv.setItem(49, items.button(Material.BARRIER, "&cClose", List.of("&7Click to close")));

        player.openInventory(inv);
    }
}
