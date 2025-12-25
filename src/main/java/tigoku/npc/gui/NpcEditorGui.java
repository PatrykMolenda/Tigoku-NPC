package tigoku.npc.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import tigoku.npc.model.NpcDefinition;
import tigoku.npc.service.NpcService;

import java.util.List;

public final class NpcEditorGui {
    public static final String TITLE_PREFIX = "NPC: Edit ";

    public static final int SLOT_NAME = 20;
    public static final int SLOT_ENTITY_TYPE = 19;
    public static final int SLOT_TYPE = 21; // villager type (only for villager)
    public static final int SLOT_PROFESSION = 22;
    public static final int SLOT_TRADES = 23;
    public static final int SLOT_MODEL_OPTION = 24;
    public static final int SLOT_LEVEL_MINUS = 30;
    public static final int SLOT_LEVEL_INFO = 31;
    public static final int SLOT_LEVEL_PLUS = 32;
    public static final int SLOT_SAVE = 49;
    public static final int SLOT_BACK = 45;
    public static final int SLOT_CLOSE = 53;

    private final NpcService npcService;
    private final GuiItemFactory items;

    public NpcEditorGui(NpcService npcService, GuiItemFactory items) {
        this.npcService = npcService;
        this.items = items;
    }

    private boolean isAgeable(EntityType type) {
        switch (type) {
            case COW:
            case SHEEP:
            case PIG:
            case HORSE:
            case WOLF:
            case CAT:
            case PARROT:
            case BEE:
            case LLAMA:
            case POLAR_BEAR:
            case FOX:
            case CHICKEN:
            case RABBIT:
                return true;
            default:
                return false;
        }
    }

    public void open(Player player, String npcId) {
        NpcDefinition def = npcService.getDefinition(npcId).orElse(null);
        if (def == null) return;

        Inventory inv = Bukkit.createInventory(player, 54, TITLE_PREFIX + def.getId());

        inv.setItem(SLOT_NAME, items.button(Material.NAME_TAG, "&eName", List.of("&7Click to rename (chat)", "&7Current: &f" + (def.getDisplayName() == null ? "-" : def.getDisplayName()))));
        inv.setItem(SLOT_ENTITY_TYPE, items.button(Material.COMPASS, "&eEntity: &f" + def.getEntityType().name(), List.of("&7Click to cycle entity type")));

        boolean isVillager = def.getEntityType() == EntityType.VILLAGER;
        if (isVillager) {
            inv.setItem(SLOT_TYPE, items.button(Material.CARVED_PUMPKIN, "&eVillager Type: &f" + def.getVillagerType().name(), List.of("&7Click to cycle type")));
            inv.setItem(SLOT_PROFESSION, items.button(Material.EMERALD, "&eProfession: &f" + def.getProfession().name(), List.of("&7Click to cycle profession")));
            inv.setItem(SLOT_TRADES, items.button(Material.CHEST, "&eEdit Trades", List.of("&7Click to edit trades (Villager only)")));

            inv.setItem(SLOT_LEVEL_MINUS, items.button(Material.RED_CONCRETE, "&c-", List.of("&7Decrease level")));
            inv.setItem(SLOT_LEVEL_INFO, items.button(Material.OAK_SIGN, "&eLevel", List.of("&f" + def.getLevel(), "", "&7Left-click: -1", "&7Right-click: -16", "&7Shift+Left-click: -64")));
            inv.setItem(SLOT_LEVEL_PLUS, items.button(Material.LIME_CONCRETE, "&a+", List.of("&7Increase level")));
        } else {
            inv.setItem(SLOT_TYPE, items.button(Material.GRAY_STAINED_GLASS_PANE, "&7Villager-only", List.of("&7These options are available only for Villagers")));
            inv.setItem(SLOT_PROFESSION, items.button(Material.GRAY_STAINED_GLASS_PANE, "&7Villager-only", null));
            inv.setItem(SLOT_TRADES, items.button(Material.GRAY_STAINED_GLASS_PANE, "&7No trades", List.of("&7Trades are supported only for Villagers")));

            inv.setItem(SLOT_LEVEL_MINUS, items.button(Material.GRAY_STAINED_GLASS_PANE, "&7-", null));
            inv.setItem(SLOT_LEVEL_INFO, items.button(Material.GRAY_STAINED_GLASS_PANE, "&7Level", List.of("&7Only for Villagers")));
            inv.setItem(SLOT_LEVEL_PLUS, items.button(Material.GRAY_STAINED_GLASS_PANE, "&7+", null));
        }

        // model option slot (shows baby toggle for ageable entities)
        boolean ageable = isAgeable(def.getEntityType());
        if (ageable) {
            boolean baby = Boolean.parseBoolean(def.getModelData().getOrDefault("baby", "false"));
            inv.setItem(SLOT_MODEL_OPTION, items.button(Material.BONE_MEAL, "&eBaby: &f" + baby, List.of("&7Click to toggle baby")));
        } else {
            inv.setItem(SLOT_MODEL_OPTION, items.button(Material.GRAY_STAINED_GLASS_PANE, "&7Model options", List.of("&7No model options for this entity")));
        }

        inv.setItem(SLOT_BACK, items.button(Material.ARROW, "&eBack", List.of("&7Back to NPC list")));
        inv.setItem(SLOT_SAVE, items.button(Material.LIME_DYE, "&aSave", List.of("&7Save changes")));
        inv.setItem(SLOT_CLOSE, items.button(Material.BARRIER, "&cClose", List.of("&7Click to close")));

        player.openInventory(inv);
    }
}
