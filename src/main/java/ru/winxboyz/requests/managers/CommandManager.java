package ru.winxboyz.requests.managers;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import ru.winxboyz.requests.ReQuests;
import ru.winxboyz.requests.utils.QuestData;
import ru.winxboyz.requests.utils.Tracker;

public class CommandManager {
    // private ReQuests plugin;;
    private final ConcurrentHashMap<UUID,List<Tracker>> progress;
    
    private final List<QuestData> quests;
    private final Map<String,String> messages;
    
    public Map<String,String> getMessages() {
        return messages;
    }

    public void handleAccept(Player player, String questName) {
        QuestData quest = quests.stream().filter(q -> q.getName().equals(questName)).findFirst().orElse(null);
        if(quest == null) {
            player.sendMessage(Component.text(messages.getOrDefault("quest-not-found","КВЕСТ НЕ НАЙДЕН")).color(NamedTextColor.RED));
            return;
        }
        if(progress.get(player.getUniqueId()).stream().anyMatch(t -> t.getQuest() == quest)) {
            player.sendMessage(Component.text(messages.getOrDefault("quest-already-accepted","КВЕСТ УЖЕ ВЗЯТ")).color(NamedTextColor.RED));
            return;
        }
        Tracker tracker = new Tracker(quest, System.currentTimeMillis());
        progress.get(player.getUniqueId()).add(tracker);
        player.sendMessage(Component.text(messages.getOrDefault("quest-accepted","КВЕСТ ВЗЯТ")).color(NamedTextColor.GREEN));
    }

    public CommandManager(ReQuests plugin) {
        // this.plugin = plugin;
        progress = plugin.getProgress();
        messages = plugin.getMessages();
        quests = plugin.getQuests();
    }

}
