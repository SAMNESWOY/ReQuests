package ru.winxboyz.requests.managers;

import ru.winxboyz.requests.ReQuests;

public class PluginManager {
    private static PluginManager instance;

    private PluginManager() {}
    
    public static PluginManager getInstance() {
        if (instance == null) {
            instance = new PluginManager();
        }
        return instance;
    }

    private CommandManager commandManager;
    private ConfigManager configManager;
    private QuestManager questManager;
    // private DatabaseManager dbManager;

    public CommandManager getCommandManager() { return commandManager; }
    public ConfigManager getConfigManager() { return configManager; }
    public QuestManager getQuestManager() { return questManager; }
    // public DatabaseManager getDatabaseManager() { return dbManager; }

    public void initialize(ReQuests plugin) {
        commandManager = new CommandManager(plugin);
        configManager = new ConfigManager(plugin);
        questManager = new QuestManager(plugin);
        // dbManager = new DatabaseManager(plugin);
    }
}
