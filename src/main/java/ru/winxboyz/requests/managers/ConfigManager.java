package ru.winxboyz.requests.managers;

import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import ru.winxboyz.requests.ReQuests;
import ru.winxboyz.requests.utils.ObjectiveType;
import ru.winxboyz.requests.utils.QuestData;
import ru.winxboyz.requests.utils.QuestObjective;
import ru.winxboyz.requests.utils.QuestReward;
import ru.winxboyz.requests.utils.RewardType;

public class ConfigManager {

    private ReQuests plugin;

    // private String dbName;

    private ConfigurationSection localeSection,sqlSection,questsSection;

    public void loadConfig() {
        
        plugin.setLocale(plugin.getConfig().getString("locale","en"));
        
        sqlSection     = plugin.getConfig().getConfigurationSection("sql");
        localeSection  = plugin.getConfig().getConfigurationSection("locales." + plugin.getLocale());
        questsSection  = plugin.getConfig().getConfigurationSection("quests");

        if(sqlSection == null) {
            plugin.getLogger().severe("No SQL connection credentials specified");
            return;
        }

        if(questsSection == null) {
            plugin.getLogger().warning("No quests specified in config, creating default quests");
            plugin.getQuests().add(
                new QuestData(
                    "sakura", "Цветение вишни", "Принесите мне 10 вишневых саженцев", "npc_sakura",
                    List.of(new QuestObjective(ObjectiveType.GIVE, "minecraft:cherry_sapling", 10)),
                    List.of(new QuestReward(RewardType.MONEY, 1000)),
                    4 * 60 * 60 * 1000L,
                    24 * 60 * 60 * 1000L
                )
            );
            for(QuestData quest : plugin.getQuests()) {
                plugin.getConfig().set("quests." + quest.getName() + ".display-name", quest.getDisplayName());
                plugin.getConfig().set("quests." + quest.getName() + ".description", quest.getDescription());
                plugin.getConfig().set("quests." + quest.getName() + ".npc", quest.getNpc());
                plugin.getConfig().set("quests." + quest.getName() + ".cooldown", quest.getCooldown());
                plugin.getConfig().set("quests." + quest.getName() + ".duration", quest.getDuration());
                for(int i = 0; i < quest.getObjectives().size(); i++) {
                    QuestObjective obj = quest.getObjectives().get(i);
                    plugin.getConfig().set("quests." + quest.getName() + ".objectives." + i + ".type", obj.getType().name());
                    plugin.getConfig().set("quests." + quest.getName() + ".objectives." + i + ".target", obj.getTarget());
                    plugin.getConfig().set("quests." + quest.getName() + ".objectives." + i + ".amount", obj.getAmount());
                }
                for(int i = 0; i < quest.getRewards().size(); i++) {
                    QuestReward reward = quest.getRewards().get(i);
                    plugin.getConfig().set("quests." + quest.getName() + ".rewards." + i + ".type", reward.getType().name());
                    if(reward.getType() == RewardType.ITEM)
                        plugin.getConfig().set("quests." + quest.getName() + ".rewards." + i + ".identifier", reward.getIdentifier());
                    plugin.getConfig().set("quests." + quest.getName() + ".rewards." + i + ".amount", reward.getAmount());
                }
            }
            plugin.saveConfig();
        } else {
            plugin.getQuests().clear();
            Set<String> keys = questsSection.getKeys(false);
            for(String key : keys) {
                String
                    name = questsSection.getString(key + ".name"),
                    displayName = questsSection.getString(key + ".display-name"),
                    description = questsSection.getString(key + ".description"),
                    npc = questsSection.getString(key + ".npc");
                long 
                    cooldown = questsSection.getLong(key + ".cooldown"),
                    duration = questsSection.getLong(key + ".duration");
                List<QuestObjective> objectives = questsSection.getConfigurationSection(key + ".objectives").getKeys(false).stream().map(i -> {
                    String type = questsSection.getString(key + ".objectives." + i + ".type");
                    String target = questsSection.getString(key + ".objectives." + i + ".target");
                    int amount = questsSection.getInt(key + ".objectives." + i + ".amount");
                    return new QuestObjective(ObjectiveType.valueOf(type), target, amount);
                }).toList();
                List<QuestReward> rewards = questsSection.getConfigurationSection(key + ".rewards").getKeys(false).stream().map(i -> {
                    String type = questsSection.getString(key + ".rewards." + i + ".type");
                    String identifier = questsSection.getString(key + ".rewards." + i + ".identifier");
                    int amount = questsSection.getInt(key + ".rewards." + i + ".amount");
                    return new QuestReward(RewardType.valueOf(type), identifier, amount);
                }).toList();
                plugin.getQuests().add(new QuestData(name, displayName, description, npc, objectives, rewards, duration, cooldown));
            }
        }

        if(localeSection != null)
            for(String key : localeSection.getKeys(false))
                plugin.getMessages().put(key, localeSection.getString(key));

        // plugin.setTableName(sqlSection.getString("locationsTable"));
        // if(dbName == null || !dbName.equals(sqlSection.getString("database"))) {
        //     dbName = sqlSection.getString("database","reore");
        //     PluginManager.getInstance().getDatabaseManager().close();
        //     PluginManager.getInstance().getDatabaseManager().connect(dbName);
        // }
        // PluginManager.getInstance().getDatabaseManager().createTableLocations().thenAccept(res -> {
        //     PluginManager.getInstance().getDatabaseManager().fetchLocations().thenAccept(locs -> {
        //         for(Location loc : locs) {
        //             ResourceData data = plugin.getResources().stream().filter(
        //                 rd -> loc.getBlock().getType().equals(rd.getResource())
        //             ).findFirst().orElse(null);
        //             if(data == null) {
        //                 plugin.getLogger().warning("Could not find resource data for block at "
        //                 + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ()
        //                 + " in world " + loc.getWorld().getName());
        //                 continue;
        //             }
        //             plugin.getProgress().put(loc, data);
        //         }
        //     });
        // });
    }

    public ConfigManager(ReQuests plugin) {
        this.plugin = plugin;
    }

}
