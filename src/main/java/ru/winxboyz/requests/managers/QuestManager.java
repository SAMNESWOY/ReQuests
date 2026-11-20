package ru.winxboyz.requests.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import ru.winxboyz.requests.ReQuests;
import ru.winxboyz.requests.utils.ObjectiveType;
import ru.winxboyz.requests.utils.QuestData;
import ru.winxboyz.requests.utils.QuestObjective;
import ru.winxboyz.requests.utils.QuestReward;
import ru.winxboyz.requests.utils.RewardType;
import ru.winxboyz.requests.utils.Tracker;

public class QuestManager {

    // private final ReQuests plugin;
    private List<QuestData> quests;
    private ConcurrentHashMap<UUID,List<Tracker>> progress;

    public boolean isNpcQuestGiver(String npcId) {
        return quests.stream().anyMatch(q -> q.getNpc().equals(npcId));
    }

    public QuestData getQuest(String npcId) {
        return quests.stream().filter(q -> q.getNpc().equals(npcId)).findFirst().orElse(null);
    }

    public void openQuest(Player player, String npcId) {
        if(isNpcQuestGiver(npcId)) {
            QuestData quest = quests.stream().filter(q -> q.getNpc().equals(npcId)).findFirst().get();
            player.openBook(quest.getBook());
        }
    }

    public boolean hasQuests(Player player) {
        return progress.containsKey(player.getUniqueId()) &&
        progress.get(player.getUniqueId()).size() > 0;
    }

    public boolean hasQuestFrom(Player player, String npcId) {
        return progress.get(player.getUniqueId()).stream().anyMatch(t -> t.getQuest().getNpc().equals(npcId));
    }

    public boolean hasObjective(QuestData quest, ObjectiveType type) {
        return quest.getObjectives().stream().anyMatch(obj -> obj.getType() == type);
    }

    public boolean hasObjective(String npc, ObjectiveType type) {
        return getQuest(npc).getObjectives().stream().anyMatch(obj -> obj.getType() == type);
    }

    public int objectiveIndex(Tracker tracker, QuestObjective objective) {
        return tracker.getQuest().getObjectives().indexOf(objective);
    }

    public ItemStack getItems(String npcId, ObjectiveType type) {
        if(
            type == ObjectiveType.KILL ||
            !isNpcQuestGiver(npcId)) return null;
        QuestObjective objective = getQuest(npcId).getObjectives().stream().filter(obj -> obj.getType() == type).findFirst().orElse(null);
        return new ItemStack(Material.matchMaterial(objective.getTarget()), objective.getAmount());
    }

    public void trackCraft(Player player, ItemStack items) {
        if(!hasQuests(player)) return;
        for(Tracker tracker : progress.get(player.getUniqueId())) {
            if(tracker.isOnCooldown()) continue;
            List<QuestObjective> objectives = tracker.getQuest().queryObjectives(ObjectiveType.CRAFT, items.getType().getKey().toString());
            if(objectives.size() == 0) continue;

            int[] tracks = tracker.getProgress();
            for(QuestObjective objective : objectives) {
                int index = objectiveIndex(tracker, objective);
                if(objective.getAmount() <= tracks[index]) continue;
                tracks[index] += items.getAmount();
                if(objective.getAmount() <= tracks[index])
                    checkCompletion(player, tracker);
            }
        }
    }

    public void trackGive(Player player, ItemStack items) {
        if(!hasQuests(player)) return;
        for(Tracker tracker : progress.get(player.getUniqueId())) {
            if(tracker.isOnCooldown()) continue;
            List<QuestObjective> objectives = tracker.getQuest().queryObjectives(ObjectiveType.GIVE, items.getType().getKey().toString());
            if(objectives.size() == 0) continue;

            int[] tracks = tracker.getProgress();
            for(QuestObjective objective : objectives) {
                int index = objectiveIndex(tracker, objective);
                if(objective.getAmount() <= tracks[index]) continue;

                ItemStack[] inventory = player.getInventory().getContents();
                for(ItemStack item : inventory) {
                    if(item != null && item.getType() == items.getType()) {
                        int amount = item.getAmount();
                        if(amount + tracks[index] >= objective.getAmount()) {
                            item.setAmount(amount + tracks[index] - objective.getAmount());
                            tracks[index] = objective.getAmount();
                            checkCompletion(player, tracker);
                            break;
                        }
                        item.setAmount(0);
                        tracks[index] += amount;
                    }
                }
            }
        }
    }

    public void checkCompletion(Player player, Tracker tracker) {
        for(int i = 0; i < tracker.getProgress().length; i++)
            if(tracker.getProgress()[i] < tracker.getQuest().getObjectives().get(i).getAmount())
                return;
        handleReward(player, tracker.getQuest());
        tracker.sendOnCooldown();
    }

    public void handleReward(Player player, QuestData quest) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
        
        Component message = Component.text("Вы выполнили квест: " + quest.getDisplayName() + "!")
            .color(NamedTextColor.GREEN)
            .append(Component.text("Награды:\n").color(NamedTextColor.YELLOW));

        for(QuestReward reward : quest.getRewards()) {
            message = message.append(Component.text((
                reward.getType() == RewardType.MONEY ?
                    "$" + reward.getAmount() :
                reward.getType() == RewardType.EXP   ?
                    reward.getAmount() + " очков опыта" :
                reward.getAmount() + " шт " + Material.matchMaterial(reward.getIdentifier()).toString()) + "\n"
            ).color(NamedTextColor.YELLOW));
            switch(reward.getType()) {
                case MONEY -> ReQuests.getEconomy().depositPlayer(player, reward.getAmount());
                case EXP -> player.giveExp(reward.getAmount());
                case ITEM -> {
                    HashMap<Integer,ItemStack> leftOvers = player.getInventory()
                    .addItem(new ItemStack(Material.matchMaterial(reward.getIdentifier()),reward.getAmount()));
                    for(ItemStack item : leftOvers.values()) {
                        player.getWorld().dropItem(player.getLocation(), item);
                    }
                }
            }
        }
        player.sendMessage(message);
    }

    public void addPlayer(Player player) {
        progress.put(player.getUniqueId(), new ArrayList<>());
    }

    public void removePlayer(Player player) {
        progress.remove(player.getUniqueId());
    }

    public QuestManager(ReQuests plugin) {
        // this.plugin = plugin;
        this.quests = plugin.getQuests();
        this.progress = plugin.getProgress();
    }

}
