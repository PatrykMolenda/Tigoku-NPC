package tigoku.npc.service;

import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import tigoku.npc.gui.GuiController;
import tigoku.npc.model.NpcDefinition;
import tigoku.npc.storage.NpcRepository;

import java.io.IOException;
import java.util.logging.Logger;

public final class GuiFacade {
    private final GuiController controller;
    private final NpcService npcService;
    private final NpcRepository repository;
    private final Logger logger;

    public GuiFacade(GuiController controller, NpcService npcService, NpcRepository repository, Logger logger) {
        this.controller = controller;
        this.npcService = npcService;
        this.repository = repository;
        this.logger = logger;
    }

    public void openNpcList(Player player) {
        controller.openNpcList(player);
    }

    public void openTradeList(Player player, String npcId) {
        controller.openTradeList(player, npcId);
    }

    public void openTradeEditor(Player player, String npcId, int tradeIndex) {
        controller.openTradeEditor(player, npcId, tradeIndex);
    }

    public void openNpcEditor(Player player, String npcId) {
        controller.openNpcEditor(player, npcId);
    }

    public void applyTrades(Villager v, NpcDefinition def) {
        npcService.applyDefinition(v, def);
    }

    public void save() {
        try {
            repository.saveAll(npcService.getDefinitions());
        } catch (IOException ex) {
            logger.warning("Failed to save npcs.yml: " + ex.getMessage());
        }
    }
}
