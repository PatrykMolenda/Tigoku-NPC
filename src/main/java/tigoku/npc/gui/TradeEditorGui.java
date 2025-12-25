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

public final class TradeEditorGui {
    public static final String TITLE_PREFIX = "NPC: Edit trade ";

    public static final int SLOT_ING1 = 20;
    public static final int SLOT_ING2 = 21;
    public static final int SLOT_RESULT = 24;

    public static final int SLOT_MAXUSES_MINUS = 37;
    public static final int SLOT_MAXUSES_INFO = 38;
    public static final int SLOT_MAXUSES_PLUS = 39;

    public static final int SLOT_SAVE = 49;
    public static final int SLOT_BACK = 45;
    public static final int SLOT_CLOSE = 53;

    private final NpcService npcService;
    private final GuiItemFactory items;

    public TradeEditorGui(NpcService npcService, GuiItemFactory items) {
        this.npcService = npcService;
        this.items = items;
    }

    public void open(Player player, String npcId, int tradeIndex) {
        NpcDefinition def = npcService.getDefinition(npcId).orElse(null);
        if (def == null) return;
        if (tradeIndex < 0 || tradeIndex >= def.getTrades().size()) return;

        TradeDefinition t = def.getTrades().get(tradeIndex);

        Inventory inv = Bukkit.createInventory(player, 54, TITLE_PREFIX + def.getId() + " #" + (tradeIndex + 1));

        inv.setItem(SLOT_ING1, placeholderOrItem(t.getIngredient1(), "&eCost (1)", "&7Click with an item on cursor"));
        inv.setItem(SLOT_ING2, placeholderOrItem(t.getIngredient2(), "&eCost (2) optional", "&7Click with an item on cursor"));
        inv.setItem(SLOT_RESULT, placeholderOrItem(t.getResult(), "&aResult", "&7Click with an item on cursor"));

        inv.setItem(SLOT_MAXUSES_MINUS, items.button(Material.RED_CONCRETE, "&c-", List.of("&7Decrease max uses")));
        inv.setItem(SLOT_MAXUSES_INFO, items.button(Material.OAK_SIGN, "&eMax uses", List.of("&f" + t.getMaxUses(), "", "&7Left-click: -1", "&7Right-click: -16", "&7Shift+Left-click: -64")));
        inv.setItem(SLOT_MAXUSES_PLUS, items.button(Material.LIME_CONCRETE, "&a+", List.of("&7Increase max uses")));

        inv.setItem(SLOT_BACK, items.button(Material.ARROW, "&eBack", List.of("&7Back to trades list")));
        inv.setItem(SLOT_SAVE, items.button(Material.LIME_DYE, "&aSave", List.of("&7Save and apply trades")));
        inv.setItem(SLOT_CLOSE, items.button(Material.BARRIER, "&cClose", List.of("&7Click to close")));

        player.openInventory(inv);
    }

    private ItemStack placeholderOrItem(ItemStack item, String title, String hint) {
        if (!ItemUtils.isEmpty(item)) return item.clone();
        return items.button(Material.GRAY_STAINED_GLASS_PANE, title, List.of(hint));
    }
}
