package tigoku.npc.gui;

import org.bukkit.entity.Player;
import tigoku.npc.service.SessionService;

public final class GuiController {
    private final SessionService sessions;
    private final NpcListGui npcListGui;
    private final TradeListGui tradeListGui;
    private final TradeEditorGui tradeEditorGui;
    private final NpcEditorGui npcEditorGui;

    public GuiController(SessionService sessions, NpcListGui npcListGui, TradeListGui tradeListGui, TradeEditorGui tradeEditorGui, NpcEditorGui npcEditorGui) {
        this.sessions = sessions;
        this.npcListGui = npcListGui;
        this.tradeListGui = tradeListGui;
        this.tradeEditorGui = tradeEditorGui;
        this.npcEditorGui = npcEditorGui;
    }

    public void openNpcList(Player player) {
        sessions.set(player, new GuiSession(GuiSession.Type.NPC_LIST, null, null));
        npcListGui.open(player);
    }

    public void openTradeList(Player player, String npcId) {
        sessions.set(player, new GuiSession(GuiSession.Type.TRADE_LIST, npcId, null));
        tradeListGui.open(player, npcId);
    }

    public void openTradeEditor(Player player, String npcId, int tradeIndex) {
        sessions.set(player, new GuiSession(GuiSession.Type.TRADE_EDITOR, npcId, tradeIndex));
        tradeEditorGui.open(player, npcId, tradeIndex);
    }

    public void openNpcEditor(Player player, String npcId) {
        sessions.set(player, new GuiSession(GuiSession.Type.NPC_EDITOR, npcId, null));
        npcEditorGui.open(player, npcId);
    }
}
