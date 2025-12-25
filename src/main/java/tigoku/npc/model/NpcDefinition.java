package tigoku.npc.model;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class NpcDefinition {
    private final String id;
    private String displayName;
    private Location location;

    private EntityType entityType = EntityType.VILLAGER;

    private Villager.Type villagerType = Villager.Type.PLAINS;
    private Villager.Profession profession = Villager.Profession.NONE;
    private int level = 1;

    private boolean ai = false;
    private boolean gravity = false;
    private boolean invulnerable = true;
    private boolean silent = true;

    private final List<TradeDefinition> trades = new ArrayList<>();
    private final java.util.Map<String, String> modelData = new java.util.HashMap<>();

    public NpcDefinition(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    public String getId() { return id; }
    public java.util.Map<String, String> getModelData() { return modelData; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public EntityType getEntityType() { return entityType; }
    public void setEntityType(EntityType entityType) { this.entityType = entityType; }

    public Villager.Type getVillagerType() { return villagerType; }
    public void setVillagerType(Villager.Type villagerType) { this.villagerType = villagerType; }

    public Villager.Profession getProfession() { return profession; }
    public void setProfession(Villager.Profession profession) { this.profession = profession; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = Math.max(1, level); }

    public boolean isAi() { return ai; }
    public void setAi(boolean ai) { this.ai = ai; }

    public boolean isGravity() { return gravity; }
    public void setGravity(boolean gravity) { this.gravity = gravity; }

    public boolean isInvulnerable() { return invulnerable; }
    public void setInvulnerable(boolean invulnerable) { this.invulnerable = invulnerable; }

    public boolean isSilent() { return silent; }
    public void setSilent(boolean silent) { this.silent = silent; }

    public List<TradeDefinition> getTrades() { return trades; }
}
