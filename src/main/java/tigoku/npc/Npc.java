package tigoku.npc;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import tigoku.npc.command.NpcCommand;
import tigoku.npc.gui.*;
import tigoku.npc.listener.GuiListener;
import tigoku.npc.listener.NpcEntityListener;
import tigoku.npc.listener.RenameListener;
import tigoku.npc.service.*;
import tigoku.npc.storage.NpcRepository;
import tigoku.npc.storage.YamlNpcRepository;

import java.io.File;
import java.io.IOException;

public final class Npc extends JavaPlugin {

    private NpcRepository repository;
    private NpcService npcService;
    private SessionService sessionService;

    private GuiFacade gui;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.repository = new YamlNpcRepository(new File(getDataFolder(), "npcs.yml"));
        TradeService tradeService = new TradeService();
        this.npcService = new NpcService(this, tradeService);
        this.sessionService = new SessionService();

        var items = new GuiItemFactory();
        var npcListGui = new NpcListGui(npcService, items);
        var tradeListGui = new TradeListGui(npcService, items);
        var tradeEditorGui = new TradeEditorGui(npcService, items);
        var npcEditorGui = new NpcEditorGui(npcService, items);
        var guiController = new GuiController(sessionService, npcListGui, tradeListGui, tradeEditorGui, npcEditorGui);
        this.gui = new GuiFacade(guiController, npcService, repository, getLogger());

        reloadInternal();

        Bukkit.getPluginManager().registerEvents(new GuiListener(sessionService, npcService, gui), this);
        Bukkit.getPluginManager().registerEvents(new NpcEntityListener(npcService, gui), this);
        Bukkit.getPluginManager().registerEvents(new RenameListener(sessionService, npcService, this, gui), this);

        PluginCommand cmd = getCommand("npc");
        if (cmd != null) {
            var executor = new NpcCommand(npcService, repository, gui, this::reloadInternal);
            cmd.setExecutor(executor);
            cmd.setTabCompleter(executor);
        }

        getLogger().info("Enabled.");
    }

    @Override
    public void onDisable() {
        if (sessionService != null) {
            sessionService.clearAll();
        }

        if (repository != null && npcService != null) {
            try {
                repository.saveAll(npcService.getDefinitions());
            } catch (IOException ex) {
                getLogger().warning("Failed to save npcs.yml: " + ex.getMessage());
            }
        }

        if (npcService != null) {
            npcService.despawnAll();
        }

        getLogger().info("Disabled.");
    }

    private void reloadInternal() {
        reloadConfig();

        if (npcService == null || repository == null) return;

        npcService.reset();

        try {
            var loaded = repository.loadAll();
            for (var def : loaded.values()) {
                npcService.putDefinition(def);
            }
            int spawned = npcService.spawnAllLoadedWorlds();
            getLogger().info("Loaded NPCs: " + loaded.size() + ", spawned: " + spawned);
        } catch (IOException ex) {
            getLogger().warning("Failed to load npcs.yml: " + ex.getMessage());
        } catch (Exception ex) {
            getLogger().warning("Failed to spawn NPCs: " + ex.getMessage());
        }
    }
}
