package tigoku.npc.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tigoku.npc.model.NpcDefinition;
import tigoku.npc.model.TradeDefinition;
import tigoku.npc.service.NpcService;
import tigoku.npc.util.ItemUtils;

import java.util.List;

public final class TradeListGui {
    public static final String TITLE_PREFIX = "NPC: Trades ";

    private final NpcService npcService;
    private final GuiItemFactory items;

    public TradeListGui(NpcService npcService, GuiItemFactory items) {
        this.npcService = npcService;
        this.items = items;
    }

    public void open(Player player, String npcId) {
        NpcDefinition def = npcService.getDefinition(npcId).orElse(null);
        if (def == null) return;

        Inventory inv = Bukkit.createInventory(player, 54, TITLE_PREFIX + def.getId());

        int slot = 0;
        for (int i = 0; i < def.getTrades().size(); i++) {
            if (slot >= 45) break;
            TradeDefinition t = def.getTrades().get(i);
            ItemStack icon = !ItemUtils.isEmpty(t.getResult()) ? t.getResult().clone() : new ItemStack(Material.PAPER);
            var meta = icon.getItemMeta();
            if (meta != null) {
                meta.setLore(List.of(
                        "§7Left-click: §fEdit",
                        "§7Right-click: §fDelete",
                        "§7Shift+Left-click: §fDuplicate"
                ));
                icon.setItemMeta(meta);
            }
            inv.setItem(slot++, icon);
        }

        inv.setItem(45, items.button(Material.ARROW, "&eBack", List.of("&7Back to NPC list")));
        inv.setItem(49, items.button(Material.EMERALD, "&aAdd trade", List.of("&7Create a new trade")));
        inv.setItem(53, items.button(Material.BARRIER, "&cClose", List.of("&7Click to close")));

        player.openInventory(inv);
    }
}
