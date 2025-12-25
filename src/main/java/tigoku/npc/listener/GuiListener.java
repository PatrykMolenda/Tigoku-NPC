package tigoku.npc.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Villager;
import tigoku.npc.gui.*;
import tigoku.npc.model.NpcDefinition;
import tigoku.npc.model.TradeDefinition;
import tigoku.npc.service.GuiFacade;
import tigoku.npc.service.NpcService;
import tigoku.npc.service.SessionService;
import tigoku.npc.util.ItemUtils;

public final class GuiListener implements Listener {
    private final SessionService sessions;
    private final NpcService npcService;
    private final GuiFacade gui;

    public GuiListener(SessionService sessions, NpcService npcService, GuiFacade gui) {
        this.sessions = sessions;
        this.npcService = npcService;
        this.gui = gui;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        GuiSession s = sessions.get(player);
        if (s == null) return;

        Inventory top = e.getView().getTopInventory();
        int topSize = top.getSize();
        int raw = e.getRawSlot();
        boolean isTopClick = raw >= 0 && raw < topSize;

        if (isTopClick) {
            e.setCancelled(true);

            if (e.getClick() == ClickType.NUMBER_KEY
                    || e.getClick() == ClickType.DOUBLE_CLICK
                    || e.getClick() == ClickType.MIDDLE
                    || e.getClick() == ClickType.CONTROL_DROP
                    || e.getAction() == InventoryAction.COLLECT_TO_CURSOR
                    || e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
                    || e.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD
                    || e.getAction() == InventoryAction.HOTBAR_SWAP
                    || e.getAction() == InventoryAction.CLONE_STACK
                    || e.getAction() == InventoryAction.UNKNOWN) {
                return;
            }

            switch (s.getType()) {
                case NPC_LIST -> handleNpcListClick(player, e);
                case TRADE_LIST -> handleTradeListClick(player, e, s);
                case TRADE_EDITOR -> handleTradeEditorClick(player, e, s);
                case NPC_EDITOR -> handleNpcEditorClick(player, e, s);
            }
        } else {
            if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
                    || e.getAction() == InventoryAction.COLLECT_TO_CURSOR
                    || e.getClick() == ClickType.DOUBLE_CLICK) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (sessions.get(player) == null) return;

        int topSize = e.getView().getTopInventory().getSize();
        for (int rawSlot : e.getRawSlots()) {
            if (rawSlot < topSize) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player player)) return;
        sessions.clear(player);
    }

    private void handleNpcListClick(Player player, InventoryClickEvent e) {
        int raw = e.getRawSlot();
        if (raw == 49) {
            player.closeInventory();
            return;
        }
        if (raw < 0 || raw >= 45) return;

        ItemStack clicked = e.getCurrentItem();
        if (ItemUtils.isEmpty(clicked)) return;

        var defs = new java.util.ArrayList<>(npcService.getDefinitions().values());
        defs.sort(java.util.Comparator.comparing(NpcDefinition::getId));
        if (raw >= defs.size()) return;

        String npcId = defs.get(raw).getId();
        if (e.getClick().isLeftClick()) {
            if (e.isShiftClick()) {
                gui.openNpcEditor(player, npcId);
            } else {
                gui.openTradeList(player, npcId);
            }
        } else if (e.getClick().isRightClick()) {
            npcService.getSpawned(npcId).ifPresent(v -> player.teleport(v.getLocation()));
        }
    }

    private void handleTradeListClick(Player player, InventoryClickEvent e, GuiSession s) {
        int raw = e.getRawSlot();
        String npcId = s.getNpcId();
        if (npcId == null) return;

        if (raw == 45) {
            gui.openNpcList(player);
            return;
        }
        if (raw == 53) {
            player.closeInventory();
            return;
        }
        if (raw == 49) {
            var def = npcService.getDefinition(npcId).orElse(null);
            if (def == null) return;
            def.getTrades().add(new TradeDefinition());
            gui.openTradeEditor(player, npcId, def.getTrades().size() - 1);
            return;
        }

        if (raw < 0 || raw >= 45) return;
        var def = npcService.getDefinition(npcId).orElse(null);
        if (def == null) return;
        if (raw >= def.getTrades().size()) return;

        if (e.getClick().isLeftClick() && e.isShiftClick()) {
            TradeDefinition src = def.getTrades().get(raw);
            var copy = new TradeDefinition();
            copy.setIngredient1(ItemUtils.cloneOrNull(src.getIngredient1()));
            copy.setIngredient2(ItemUtils.cloneOrNull(src.getIngredient2()));
            copy.setResult(ItemUtils.cloneOrNull(src.getResult()));
            copy.setMaxUses(src.getMaxUses());
            copy.setRewardExp(src.isRewardExp());
            copy.setVillagerXp(src.getVillagerXp());
            copy.setPriceMultiplier(src.getPriceMultiplier());
            def.getTrades().add(copy);
            gui.openTradeList(player, npcId);
            return;
        }

        if (e.getClick().isLeftClick()) {
            gui.openTradeEditor(player, npcId, raw);
            return;
        }

        if (e.getClick().isRightClick()) {
            def.getTrades().remove(raw);
            gui.openTradeList(player, npcId);
        }
    }

    private void handleTradeEditorClick(Player player, InventoryClickEvent e, GuiSession s) {
        String npcId = s.getNpcId();
        Integer idx = s.getTradeIndex();
        if (npcId == null || idx == null) return;

        var def = npcService.getDefinition(npcId).orElse(null);
        if (def == null) return;
        if (idx < 0 || idx >= def.getTrades().size()) return;
        TradeDefinition t = def.getTrades().get(idx);

        int raw = e.getRawSlot();
        if (raw == TradeEditorGui.SLOT_BACK) {
            gui.openTradeList(player, npcId);
            return;
        }
        if (raw == TradeEditorGui.SLOT_CLOSE) {
            player.closeInventory();
            return;
        }
        if (raw == TradeEditorGui.SLOT_SAVE) {
            if (ItemUtils.isEmpty(t.getIngredient1()) || ItemUtils.isEmpty(t.getResult())) {
                player.sendMessage("§cA trade must have Cost (1) and Result.");
                return;
            }
            npcService.getSpawnedEntity(npcId).ifPresent(v -> npcService.applyDefinition(v, def));
            gui.save();
            player.sendMessage("§aTrade saved.");
            gui.openTradeList(player, npcId);
            return;
        }

        if (raw == TradeEditorGui.SLOT_ING1 || raw == TradeEditorGui.SLOT_ING2 || raw == TradeEditorGui.SLOT_RESULT) {
            ItemStack cursor = e.getCursor();
            if (ItemUtils.isEmpty(cursor)) {
                if (raw == TradeEditorGui.SLOT_ING1) t.setIngredient1(null);
                if (raw == TradeEditorGui.SLOT_ING2) t.setIngredient2(null);
                if (raw == TradeEditorGui.SLOT_RESULT) t.setResult(null);
            } else {
                ItemStack placed = cursor.clone();
                if (raw == TradeEditorGui.SLOT_ING1) t.setIngredient1(placed);
                if (raw == TradeEditorGui.SLOT_ING2) t.setIngredient2(placed);
                if (raw == TradeEditorGui.SLOT_RESULT) t.setResult(placed);
            }
            gui.openTradeEditor(player, npcId, idx);
            return;
        }

        if (raw == TradeEditorGui.SLOT_MAXUSES_MINUS || raw == TradeEditorGui.SLOT_MAXUSES_PLUS || raw == TradeEditorGui.SLOT_MAXUSES_INFO) {
            int delta;
            if (raw == TradeEditorGui.SLOT_MAXUSES_PLUS) {
                delta = clickDelta(e);
            } else {
                delta = -clickDelta(e);
            }
            t.setMaxUses(Math.max(1, t.getMaxUses() + delta));
            gui.openTradeEditor(player, npcId, idx);
        }
    }

    private void handleNpcEditorClick(Player player, InventoryClickEvent e, GuiSession s) {
        String npcId = s.getNpcId();
        if (npcId == null) return;

        var def = npcService.getDefinition(npcId).orElse(null);
        if (def == null) return;

        int raw = e.getRawSlot();
        if (raw == NpcEditorGui.SLOT_BACK) {
            gui.openNpcList(player);
            return;
        }
        if (raw == NpcEditorGui.SLOT_CLOSE) {
            player.closeInventory();
            return;
        }
        if (raw == NpcEditorGui.SLOT_SAVE) {
            npcService.putDefinition(def);
            npcService.getSpawnedEntity(npcId).ifPresent(v -> npcService.applyDefinition(v, def));
            gui.save();
            player.sendMessage("§aNPC saved.");
            gui.openNpcList(player);
            return;
        }

        if (raw == NpcEditorGui.SLOT_NAME) {
            player.closeInventory();
            player.sendMessage("§eType the new name in chat.");
            sessions.startRename(player, npcId);
            return;
        }

        if (raw == NpcEditorGui.SLOT_ENTITY_TYPE) {
            var next = nextEntityType(def.getEntityType());
            def.setEntityType(next);
            gui.openNpcEditor(player, npcId);
            return;
        }

        if (raw == NpcEditorGui.SLOT_TYPE) {
            var next = nextType(def.getVillagerType());
            def.setVillagerType(next);
            gui.openNpcEditor(player, npcId);
            return;
        }

        if (raw == NpcEditorGui.SLOT_PROFESSION) {
            var next = nextProfession(def.getProfession());
            def.setProfession(next);
            gui.openNpcEditor(player, npcId);
            return;
        }

        if(raw == NpcEditorGui.SLOT_TRADES) {
            if (def.getEntityType() == org.bukkit.entity.EntityType.VILLAGER) {
                gui.openTradeList(player, npcId);
            } else {
                player.sendMessage("§cTrades are supported only for Villagers.");
            }
            return;
        }
        if (raw == NpcEditorGui.SLOT_MODEL_OPTION) {
            boolean ageable = false;
            try {
                ageable = switch (def.getEntityType()) {
                    case COW, SHEEP, PIG, HORSE, WOLF, CAT, PARROT, BEE, LLAMA, POLAR_BEAR, FOX, CHICKEN, RABBIT -> true;
                    default -> false;
                };
            } catch (Exception ignored) {}
            if (ageable) {
                boolean cur = Boolean.parseBoolean(def.getModelData().getOrDefault("baby", "false"));
                def.getModelData().put("baby", Boolean.toString(!cur));
                gui.openNpcEditor(player, npcId);
            } else {
                player.sendMessage("§cNo model options for this entity.");
            }
            return;
        }

        if (raw == NpcEditorGui.SLOT_LEVEL_MINUS || raw == NpcEditorGui.SLOT_LEVEL_PLUS || raw == NpcEditorGui.SLOT_LEVEL_INFO) {
            int delta = clickDelta(e);
            if (raw == NpcEditorGui.SLOT_LEVEL_MINUS) delta = -delta;
            def.setLevel(Math.max(1, def.getLevel() + delta));
            gui.openNpcEditor(player, npcId);
        }
    }

    private int clickDelta(InventoryClickEvent e) {
        if (e.isShiftClick()) return 64;
        if (e.getClick().isRightClick()) return 16;
        return 1;
    }

    private Villager.Type nextType(Villager.Type type) {
        var values = Villager.Type.values();
        int nextOrdinal = (type.ordinal() + 1) % values.length;
        return values[nextOrdinal];
    }

    private Villager.Profession nextProfession(Villager.Profession profession) {
        var values = Villager.Profession.values();
        int nextOrdinal = (profession.ordinal() + 1) % values.length;
        return values[nextOrdinal];
    }

    private org.bukkit.entity.EntityType nextEntityType(org.bukkit.entity.EntityType current) {
        org.bukkit.entity.EntityType[] allowed = new org.bukkit.entity.EntityType[]{
                org.bukkit.entity.EntityType.VILLAGER,
                org.bukkit.entity.EntityType.ZOMBIE,
                org.bukkit.entity.EntityType.SKELETON,
                org.bukkit.entity.EntityType.COW,
                org.bukkit.entity.EntityType.SHEEP,
                org.bukkit.entity.EntityType.PIG,
                org.bukkit.entity.EntityType.HORSE,
                org.bukkit.entity.EntityType.WOLF,
                org.bukkit.entity.EntityType.CAT,
                org.bukkit.entity.EntityType.PARROT,
                org.bukkit.entity.EntityType.BEE,
                org.bukkit.entity.EntityType.LLAMA,
                org.bukkit.entity.EntityType.POLAR_BEAR,
                org.bukkit.entity.EntityType.FOX,
                org.bukkit.entity.EntityType.IRON_GOLEM
        };
        int idx = 0;
        for (int i = 0; i < allowed.length; i++) {
            if (allowed[i] == current) {
                idx = i;
                break;
            }
        }
        return allowed[(idx + 1) % allowed.length];
    }
}
