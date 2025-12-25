package tigoku.npc.gui;

public final class GuiSession {
    public enum Type {
        NPC_LIST,
        TRADE_LIST,
        TRADE_EDITOR,
        NPC_EDITOR
    }

    private final Type type;
    private final String npcId;
    private final Integer tradeIndex;

    public GuiSession(Type type, String npcId, Integer tradeIndex) {
        this.type = type;
        this.npcId = npcId;
        this.tradeIndex = tradeIndex;
    }

    public Type getType() { return type; }
    public String getNpcId() { return npcId; }
    public Integer getTradeIndex() { return tradeIndex; }
}
