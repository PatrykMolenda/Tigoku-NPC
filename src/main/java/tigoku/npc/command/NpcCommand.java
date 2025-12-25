package tigoku.npc.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import tigoku.npc.Permissions;
import tigoku.npc.service.GuiFacade;
import tigoku.npc.service.NpcService;
import tigoku.npc.storage.NpcRepository;

import java.io.IOException;
import java.util.*;

public final class NpcCommand implements CommandExecutor, TabCompleter {
    private final NpcService npcService;
    private final NpcRepository repo;
    private final GuiFacade gui;
    private final Runnable reloadFn;

    public NpcCommand(NpcService npcService, NpcRepository repo, GuiFacade gui, Runnable reloadFn) {
        this.npcService = npcService;
        this.repo = repo;
        this.gui = gui;
        this.reloadFn = reloadFn;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§e/npc create <id> [name...]§7 - creates an NPC at your location");
            sender.sendMessage("§e/npc list§7 - opens the NPC list GUI");
            sender.sendMessage("§e/npc edit <id>§7 - opens the trades GUI for an NPC");
            sender.sendMessage("§e/npc remove <id>§7 - removes an NPC");
            sender.sendMessage("§e/npc reload§7 - reloads the plugin");
            sender.sendMessage("Hints: open NPC list with /npc list, then Shift+Left-click an NPC to open the NPC editor. Click 'Name' in editor to rename via chat.");
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "create" -> {
                if (!(sender instanceof Player p)) {
                    sender.sendMessage("Players only.");
                    return true;
                }
                if (!has(p, Permissions.CREATE)) return true;
                if (args.length < 2) {
                    p.sendMessage("§cUsage: /npc create <id> [name...]");
                    return true;
                }
                String id = args[1];
                if (npcService.exists(id)) {
                    p.sendMessage("§cAn NPC with that id already exists.");
                    return true;
                }
                String name = args.length >= 3 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : id;
                Location loc = p.getLocation();
                npcService.createAt(id, name, loc);
                saveSilently();
                p.sendMessage("§aNPC created: §f" + id + "§a. Sneak + Right Click the NPC to edit trades. Or use /npc edit " + id + " to edit properties.");
                return true;
            }
            case "list" -> {
                if (!(sender instanceof Player p)) return true;
                if (!has(p, Permissions.LIST)) return true;
                gui.openNpcList(p);
                return true;
            }
            case "edit" -> {
                if (!(sender instanceof Player p)) return true;
                if (!has(p, Permissions.EDIT)) return true;
                if (args.length < 2) {
                    p.sendMessage("§cUsage: /npc edit <id>");
                    return true;
                }
                String id = args[1];
                if (npcService.getDefinition(id).isEmpty()) {
                    p.sendMessage("§cNPC not found.");
                    return true;
                }
                gui.openNpcEditor(p, id);
                return true;
            }
            case "remove" -> {
                if (!has(sender, Permissions.REMOVE)) return true;
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /npc remove <id>");
                    return true;
                }
                String id = args[1];
                npcService.despawn(id);
                npcService.removeDefinition(id);
                saveSilently();
                sender.sendMessage("§aRemoved NPC: §f" + id);
                return true;
            }
            case "tp" -> {
                if (!(sender instanceof Player p)) return true;
                if (!has(p, Permissions.TP)) return true;
                if (args.length < 2) {
                    p.sendMessage("§cUsage: /npc tp <id>");
                    return true;
                }
                String id = args[1];
                npcService.getSpawned(id).ifPresentOrElse(v -> p.teleport(v.getLocation()), () -> p.sendMessage("§cNPC is not spawned."));
                return true;
            }
            case "reload" -> {
                if (!has(sender, Permissions.RELOAD)) return true;
                reloadFn.run();
                sender.sendMessage("§aReloaded NPC plugin.");
                return true;
            }
            default -> {
                sender.sendMessage("§cUnknown subcommand.");
                return true;
            }
        }
    }

    private boolean has(CommandSender sender, String perm) {
        if (sender.hasPermission(Permissions.ADMIN) || sender.hasPermission(perm)) return true;
        sender.sendMessage("§cMissing permission: " + perm);
        return false;
    }

    private void saveSilently() {
        try {
            repo.saveAll(npcService.getDefinitions());
        } catch (IOException ex) {
            Bukkit.getLogger().warning("[NPC] Failed to save npcs.yml: " + ex.getMessage());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return prefix(args[0], List.of("create", "list", "edit", "remove", "tp", "reload"));
        }
        if (args.length == 2 && List.of("edit", "remove", "tp").contains(args[0].toLowerCase(Locale.ROOT))) {
            return prefix(args[1], new ArrayList<>(npcService.getDefinitions().keySet()));
        }
        return List.of();
    }

    private List<String> prefix(String token, List<String> options) {
        String t = token.toLowerCase(Locale.ROOT);
        var out = new ArrayList<String>();
        for (String o : options) {
            if (o.toLowerCase(Locale.ROOT).startsWith(t)) out.add(o);
        }
        return out;
    }
}
