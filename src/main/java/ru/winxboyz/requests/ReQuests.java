package ru.winxboyz.requests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.milkbowl.vault.economy.Economy;
import ru.winxboyz.requests.commands.ReQuestsCommand;
import ru.winxboyz.requests.listeners.ItemCraftListener;
import ru.winxboyz.requests.listeners.NpcInteractListener;
import ru.winxboyz.requests.listeners.PlayerListener;
import ru.winxboyz.requests.managers.PluginManager;
import ru.winxboyz.requests.tasks.RemoveExpired;
import ru.winxboyz.requests.utils.QuestData;
import ru.winxboyz.requests.utils.Tracker;

public class ReQuests extends JavaPlugin {

    private final PluginManager pluginManager = PluginManager.getInstance();
    private String locale;

    private final Map<String,String> messages = new HashMap<>();
    private final List<QuestData> quests = new ArrayList<>();
    private final ConcurrentHashMap<UUID,List<Tracker>> progress = new ConcurrentHashMap<>();

    private static Economy econ = null;

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public List<QuestData> getQuests() {
        return List.copyOf(quests);
    }

    public ConcurrentHashMap<UUID,List<Tracker>> getProgress() {
        return progress;
    }
    public Map<String,String> getMessages() {
        return Map.copyOf(messages);
    }

    public void reload() {
        reloadConfig();
        progress.clear();
        pluginManager.getConfigManager().loadConfig();
    }


    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onEnable() {
        if (!setupEconomy() ) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        saveDefaultConfig();

        locale = getConfig().getString("locale","en");
        pluginManager.initialize(this);
        pluginManager.getConfigManager().loadConfig();
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(new ReQuestsCommand(this).initialize());
        });
        getServer().getPluginManager().registerEvents(new NpcInteractListener(pluginManager.getQuestManager()), this);
        getServer().getPluginManager().registerEvents(new ItemCraftListener(pluginManager.getQuestManager()), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(pluginManager.getQuestManager()), this);

        
        RemoveExpired removeExpired = new RemoveExpired(progress);

        Bukkit.getScheduler().runTaskTimer(this, removeExpired::tick, 1000L, 1000L);
        getLogger().info(getName() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        progress.clear();
        Bukkit.getScheduler().cancelTasks(this);
        // pluginManager.getDatabaseManager().close();
        getLogger().info(getName() + " has been disabled!");
    }
    
}
