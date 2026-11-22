package ru.winxboyz.requests.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import ru.winxboyz.requests.ReQuests;
import ru.winxboyz.requests.utils.QuestData;
import ru.winxboyz.requests.utils.Tracker;

public class CommandManager {
    private final ReQuests plugin;
    private final Map<UUID,List<Tracker>> progress;

    /**
     * Данные читаем у плагина на лету: менеджер создаётся до загрузки конфига,
     * поэтому копия в конструкторе оставалась null. Так же работает reload.
     */
    private List<QuestData> quests() {
        List<QuestData> loaded = plugin.getQuests();
        return loaded == null ? List.of() : loaded;
    }

    private Map<String,String> messages() {
        Map<String,String> loaded = plugin.getMessages();
        return loaded == null ? Map.of() : loaded;
    }

    public Map<String,String> getMessages() {
        return messages();
    }

    public void handleAccept(Player player, String questName) {
        QuestData quest = quests().stream().filter(q -> q.getName().equals(questName)).findFirst().orElse(null);
        if(quest == null) {
            player.sendMessage(Component.text(messages().getOrDefault("quest-not-found","КВЕСТ НЕ НАЙДЕН")).color(NamedTextColor.RED));
            return;
        }
        // Списка прогресса может не быть: игрок ещё ни одного квеста не брал.
        List<Tracker> trackers = progress.computeIfAbsent(player.getUniqueId(), key -> new ArrayList<>());
        if(trackers.stream().anyMatch(t -> t.getQuest() == quest)) {
            player.sendMessage(Component.text(messages().getOrDefault("quest-already-accepted","КВЕСТ УЖЕ ВЗЯТ")).color(NamedTextColor.RED));
            return;
        }
        Tracker tracker = new Tracker(quest, System.currentTimeMillis());
        trackers.add(tracker);
        player.sendMessage(Component.text(messages().getOrDefault("quest-accepted","КВЕСТ ВЗЯТ")).color(NamedTextColor.GREEN));
    }

    public void handleList(CommandSender sender) {
        Component message = Component.text("Команды: ");
        for(QuestData q : quests())
            message = message.append(Component.text("\n" + q.getDisplayName() + " (" + q.getName() + ")"));
        sender.sendMessage(message);
    }

    public CommandManager(ReQuests plugin) {
        this.plugin = plugin;
        this.progress = plugin.getProgress();
    }

}
