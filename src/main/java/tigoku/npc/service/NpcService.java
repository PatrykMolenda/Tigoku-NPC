package tigoku.npc.service;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import tigoku.npc.model.NpcDefinition;
import tigoku.npc.util.Keys;
import tigoku.npc.util.Text;

import java.util.*;

public final class NpcService {
    private final Plugin plugin;
    private final TradeService tradeService;

    private final Map<String, NpcDefinition> definitions = new HashMap<>();
    private final Map<UUID, String> entityToId = new HashMap<>();
    private final Map<String, UUID> idToEntity = new HashMap<>();

    public NpcService(Plugin plugin, TradeService tradeService) {
        this.plugin = plugin;
        this.tradeService = tradeService;
    }

    public Map<String, NpcDefinition> getDefinitions() {
        return Collections.unmodifiableMap(definitions);
    }

    public Optional<NpcDefinition> getDefinition(String id) {
        return Optional.ofNullable(definitions.get(id.toLowerCase(Locale.ROOT)));
    }

    public boolean exists(String id) {
        return definitions.containsKey(id.toLowerCase(Locale.ROOT));
    }

    public void putDefinition(NpcDefinition def) {
        definitions.put(def.getId().toLowerCase(Locale.ROOT), def);
    }

    public void removeDefinition(String id) {
        definitions.remove(id.toLowerCase(Locale.ROOT));
    }

    public void clearDefinitions() {
        definitions.clear();
    }

    public void reset() {
        despawnAll();
        clearDefinitions();
    }

    public Optional<Entity> getSpawnedEntity(String id) {
        UUID uuid = idToEntity.get(id.toLowerCase(Locale.ROOT));
        if (uuid == null) return Optional.empty();
        Entity e = Bukkit.getEntity(uuid);
        if (e != null && e.isValid()) return Optional.of(e);
        return Optional.empty();
    }

    public Optional<Villager> getSpawned(String id) {
        UUID uuid = idToEntity.get(id.toLowerCase(Locale.ROOT));
        if (uuid == null) return Optional.empty();
        Entity e = Bukkit.getEntity(uuid);
        if (e instanceof Villager v && e.isValid()) return Optional.of(v);
        return Optional.empty();
    }

    public Optional<String> getNpcId(Entity entity) {
        if (entity == null) return Optional.empty();
        var pdc = entity.getPersistentDataContainer();
        String id = pdc.get(Keys.npcId(plugin), PersistentDataType.STRING);
        if (id != null) return Optional.of(id);
        return Optional.ofNullable(entityToId.get(entity.getUniqueId()));
    }

    public void applyDefinition(Entity e, NpcDefinition def) {
        String name = def.getDisplayName();
        if (name == null || name.isBlank()) {
            e.setCustomName(null);
            e.setCustomNameVisible(false);
        } else {
            e.setCustomName(Text.color(name));
            e.setCustomNameVisible(true);
        }

        // set AI on mobs only
        if (e instanceof org.bukkit.entity.Mob mob) {
            mob.setAI(def.isAi());
        }

        // generic entity flags
        e.setGravity(def.isGravity());
        e.setInvulnerable(def.isInvulnerable());
        e.setSilent(def.isSilent());

        e.setPersistent(true);
        // ensure entity is persistent; removeWhenFarAway and collidable are API-version specific
        // Older/newer Paper/Spigot APIs might not expose setRemoveWhenFarAway/setCollidable on Entity
        // so we avoid calling them directly here to keep compatibility.

        if (e instanceof Villager v) {
            v.setVillagerType(def.getVillagerType());
            v.setProfession(def.getProfession());
            v.setVillagerLevel(def.getLevel());
            tradeService.applyTrades(v, def.getTrades());
        } else {
            // apply some generic model options from modelData
            var md = def.getModelData();
            if (!md.isEmpty()) {
                // example: set baby for Ageable
                if (e instanceof org.bukkit.entity.Ageable ageable) {
                    String baby = md.get("baby");
                    if (baby != null) {
                        boolean b = Boolean.parseBoolean(baby);
                        if (b) ageable.setBaby(); else ageable.setAdult();
                    }
                }
                // example: set variant/nameable for some entities could be added here
            }
        }
    }

    public Entity spawnFromDefinition(NpcDefinition def) {
        Location loc = def.getLocation();
        if (loc == null || loc.getWorld() == null) {
            throw new IllegalStateException("NPC missing location: " + def.getId());
        }

        despawn(def.getId());

        EntityType type = def.getEntityType() == null ? EntityType.VILLAGER : def.getEntityType();
        Entity e = loc.getWorld().spawnEntity(loc, type);
        tagEntity(e, def.getId());
        applyDefinition(e, def);
        return e;
    }

    public Entity createAt(String id, String displayName, Location location) {
        var def = new NpcDefinition(id);
        def.setDisplayName(displayName);
        def.setLocation(location);
        putDefinition(def);
        return spawnFromDefinition(def);
    }

    public void tagEntity(Entity e, String npcId) {
        var pdc = e.getPersistentDataContainer();
        pdc.set(Keys.npcId(plugin), PersistentDataType.STRING, npcId);
        entityToId.put(e.getUniqueId(), npcId);
        idToEntity.put(npcId.toLowerCase(Locale.ROOT), e.getUniqueId());
    }

    public void untagEntity(Entity e) {
        getNpcId(e).ifPresent(id -> {
            entityToId.remove(e.getUniqueId());
            idToEntity.remove(id.toLowerCase(Locale.ROOT));
        });
    }

    public void despawnAll() {
        for (UUID uuid : new ArrayList<>(entityToId.keySet())) {
            Entity e = Bukkit.getEntity(uuid);
            if (e != null) {
                e.remove();
            }
        }
        entityToId.clear();
        idToEntity.clear();
    }

    public void despawn(String id) {
        UUID uuid = idToEntity.remove(id.toLowerCase(Locale.ROOT));
        if (uuid == null) return;
        entityToId.remove(uuid);
        Entity e = Bukkit.getEntity(uuid);
        if (e != null) e.remove();
    }

    public int spawnAllLoadedWorlds() {
        int spawned = 0;
        for (NpcDefinition def : definitions.values()) {
            Location loc = def.getLocation();
            if (loc == null) continue;
            World world = loc.getWorld();
            if (world == null) continue;
            spawnFromDefinition(def);
            spawned++;
        }
        return spawned;
    }
}
