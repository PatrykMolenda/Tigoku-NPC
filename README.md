# Tigoku-NPC (Paper/Spigot 1.20+)

A production-ready plugin for creating **custom NPCs** (Villagers and other mobs) and managing their **custom trades** through a clean, intuitive GUI.

## Features
- NPCs support multiple entity types (Villager, Zombie, Skeleton, Cow, Sheep, Pig, Horse, Wolf, Cat, and more)
- Villager NPCs have full trade support with custom items
- AI disabled by default (static NPCs)
- No despawn: persistent entities
- Unique NPC id and custom display name
- GUI-based NPC and trade editing (no commands needed for editing)
- Model options for entities (e.g., Baby toggle for ageable mobs)
- YAML storage: `plugins/NPC/npcs.yml`

## Commands
| Command | Description |
|---------|-------------|
| `/npc create <id> [name...]` | Create an NPC at your location |
| `/npc list` | Open NPC list GUI |
| `/npc edit <id>` | Open trades GUI for an NPC |
| `/npc editnpc <id>` | Open NPC properties editor (name/type/profession/level) |
| `/npc remove <id>` | Remove NPC and definition |
| `/npc tp <id>` | Teleport to NPC |
| `/npc reload` | Reload config and NPCs |

## Permissions
| Permission | Description |
|------------|-------------|
| `npc.admin` | Full access to all NPC commands |
| `npc.create` | Create NPCs |
| `npc.edit` | Edit NPCs and trades |
| `npc.remove` | Remove NPCs |
| `npc.list` | Open NPC list |
| `npc.tp` | Teleport to NPCs |
| `npc.reload` | Reload plugin |

## In-game Usage (Admin UX)

### Creating an NPC
1. Stand where you want the NPC to spawn
2. Run `/npc create <id> [name]` (e.g., `/npc create blacksmith Blacksmith`)

### Editing NPCs
- **Method 1:** Run `/npc list` → Left-click on NPC to open trade list, or Shift+Left-click to open NPC editor
- **Method 2:** Run `/npc editnpc <id>` to open NPC properties editor directly
- **Method 3:** Sneak + Right-click an NPC in-world to open trade list

### NPC Editor GUI (Shift+Left-click or `/npc editnpc`)
- **Name** (Name Tag icon) - Click to rename via chat
- **Entity Type** (Compass icon) - Click to cycle through available entity types
- **Villager Type** (Pumpkin icon) - Villager biome variant (only for Villagers)
- **Profession** (Emerald icon) - Villager profession (only for Villagers)
- **Level** (+/- buttons) - Villager level (only for Villagers)
- **Model Options** (Bone Meal icon) - Toggle baby/adult for ageable mobs
- **Edit Trades** (Chest icon) - Open trade editor (only for Villagers)
- **Save** - Save changes to file and apply to spawned NPC

### Trade Editor GUI
- **Cost (1)** - Required item slot (click with item on cursor to set)
- **Cost (2)** - Optional second required item
- **Result** - Item player receives
- **Max Uses** (+/- buttons) - Maximum times trade can be used
- Click empty slot to clear it
- **Save** - Save trade and return to trade list

### Supported Entity Types
Villager, Zombie, Skeleton, Cow, Sheep, Pig, Horse, Wolf, Cat, Parrot, Bee, Llama, Polar Bear, Fox, Iron Golem

## Files
- `config.yml` - Message prefix (reserved for future config)
- `npcs.yml` - NPC definitions, trades, and model data (Bukkit ItemStack serialization)

## Example npcs.yml
```yaml
npcs:
  blacksmith:
    displayName: "&6Blacksmith"
    location:
      world: world
      x: 100.5
      y: 64.0
      z: 200.5
    entity:
      type: VILLAGER
    villager:
      type: PLAINS
      profession: WEAPONSMITH
      level: 5
    trades:
      - ingredient1: {type: DIAMOND, amount: 5}
        result: {type: DIAMOND_SWORD}
        maxUses: 100
```

## Ideas for Extensions
- Per-player cooldown per trade
- Per-player usage limits
- More model options (sheep color, horse armor, etc.)
- Pagination + search in NPC list
- Edit lock (prevent multiple admins editing same NPC)
- Optional Citizens integration
- Equipment slots for humanoid mobs
