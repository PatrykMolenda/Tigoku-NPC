package tigoku.npc.storage;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.EntityType;
import tigoku.npc.model.NpcDefinition;
import tigoku.npc.model.TradeDefinition;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class YamlNpcRepository implements NpcRepository {
    private final File file;

    public YamlNpcRepository(File file) {
        this.file = file;
    }

    @Override
    public Map<String, NpcDefinition> loadAll() throws IOException {
        ensureExists();
        var cfg = YamlConfiguration.loadConfiguration(file);

        var result = new HashMap<String, NpcDefinition>();
        var npcsSec = cfg.getConfigurationSection("npcs");
        if (npcsSec == null) return result;

        for (String id : npcsSec.getKeys(false)) {
            var sec = npcsSec.getConfigurationSection(id);
            if (sec == null) continue;

            var def = new NpcDefinition(id);
            def.setDisplayName(sec.getString("displayName", id));

            var locSec = sec.getConfigurationSection("location");
            if (locSec != null) {
                String worldName = locSec.getString("world");
                World world = worldName == null ? null : Bukkit.getWorld(worldName);
                if (world != null) {
                    def.setLocation(new Location(
                            world,
                            locSec.getDouble("x"),
                            locSec.getDouble("y"),
                            locSec.getDouble("z"),
                            (float) locSec.getDouble("yaw"),
                            (float) locSec.getDouble("pitch")
                    ));
                }
            }

            def.setAi(sec.getBoolean("flags.ai", false));
            def.setGravity(sec.getBoolean("flags.gravity", false));
            def.setInvulnerable(sec.getBoolean("flags.invulnerable", true));
            def.setSilent(sec.getBoolean("flags.silent", true));

            String entityType = sec.getString("entity.type", "VILLAGER");
            try { def.setEntityType(EntityType.valueOf(entityType)); } catch (IllegalArgumentException ignored) {}

            // load model data map
            var modelSec = sec.getConfigurationSection("model");
            if (modelSec != null) {
                for (String key : modelSec.getKeys(false)) {
                    def.getModelData().put(key, modelSec.getString(key));
                }
            }

            String type = sec.getString("villager.type", "PLAINS");
            String prof = sec.getString("villager.profession", "NONE");
            def.setLevel(sec.getInt("villager.level", 1));
            try { def.setVillagerType(org.bukkit.entity.Villager.Type.valueOf(type)); } catch (IllegalArgumentException ignored) {}
            try { def.setProfession(org.bukkit.entity.Villager.Profession.valueOf(prof)); } catch (IllegalArgumentException ignored) {}

            List<Map<?, ?>> trades = sec.getMapList("trades");
            for (Map<?, ?> t : trades) {
                var td = new TradeDefinition();
                td.setMaxUses(asInt(t.get("maxUses"), 999999));
                td.setRewardExp(asBoolean(t.get("rewardExp"), false));
                td.setVillagerXp(asInt(t.get("villagerXp"), 0));
                td.setPriceMultiplier(asFloat(t.get("priceMultiplier"), 0.0f));

                td.setIngredient1((ItemStack) t.get("ingredient1"));
                td.setIngredient2((ItemStack) t.get("ingredient2"));
                td.setResult((ItemStack) t.get("result"));

                def.getTrades().add(td);
            }

            result.put(id, def);
        }

        return result;
    }

    @Override
    public void saveAll(Map<String, NpcDefinition> npcs) throws IOException {
        ensureExists();
        var cfg = new YamlConfiguration();
        cfg.set("schema", 1);

        var npcsSec = cfg.createSection("npcs");
        for (var entry : npcs.entrySet()) {
            var def = entry.getValue();
            var sec = npcsSec.createSection(entry.getKey());

            sec.set("displayName", def.getDisplayName());

            var loc = def.getLocation();
            if (loc != null && loc.getWorld() != null) {
                var locSec = sec.createSection("location");
                locSec.set("world", loc.getWorld().getName());
                locSec.set("x", loc.getX());
                locSec.set("y", loc.getY());
                locSec.set("z", loc.getZ());
                locSec.set("yaw", loc.getYaw());
                locSec.set("pitch", loc.getPitch());
            }

            sec.set("flags.ai", def.isAi());
            sec.set("flags.gravity", def.isGravity());
            sec.set("flags.invulnerable", def.isInvulnerable());
            sec.set("flags.silent", def.isSilent());

            sec.set("entity.type", def.getEntityType() == null ? "VILLAGER" : def.getEntityType().name());

            // save model data
            var modelSection = sec.createSection("model");
            for (var me : def.getModelData().entrySet()) {
                modelSection.set(me.getKey(), me.getValue());
            }

            sec.set("villager.type", def.getVillagerType().name());
            sec.set("villager.profession", def.getProfession().name());
            sec.set("villager.level", def.getLevel());

            var list = new java.util.ArrayList<Map<String, Object>>();
            for (var t : def.getTrades()) {
                var map = new java.util.LinkedHashMap<String, Object>();
                map.put("ingredient1", t.getIngredient1());
                map.put("ingredient2", t.getIngredient2());
                map.put("result", t.getResult());
                map.put("maxUses", t.getMaxUses());
                map.put("rewardExp", t.isRewardExp());
                map.put("villagerXp", t.getVillagerXp());
                map.put("priceMultiplier", t.getPriceMultiplier());
                list.add(map);
            }
            sec.set("trades", list);
        }

        cfg.save(file);
    }

    private void ensureExists() throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Nie można utworzyć folderu: " + parent);
        }
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Nie można utworzyć pliku: " + file);
        }
    }

    private static int asInt(Object v, int def) {
        if (v instanceof Number n) return n.intValue();
        return def;
    }

    private static boolean asBoolean(Object v, boolean def) {
        if (v instanceof Boolean b) return b;
        return def;
    }

    private static float asFloat(Object v, float def) {
        if (v instanceof Number n) return n.floatValue();
        return def;
    }
}
