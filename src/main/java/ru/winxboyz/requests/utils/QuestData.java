package ru.winxboyz.requests.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class QuestData {
    private final String name, displayName, description, npc;
    private final List<QuestObjective> objectives;
    private final List<QuestReward> rewards;
    private final long cooldown, duration;
    private final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

    public long getCooldown() {
        return cooldown;
    }

    public long getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public ItemStack getBook() {
        return book.clone();
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getNpc() {
        return npc;
    }

    public List<QuestObjective> getObjectives() {
        return List.copyOf(objectives);
    }

    public List<QuestObjective> queryObjectives(ObjectiveType type, String target, int amount) {
        return objectives.stream()
        .filter(obj ->
            obj.getType() == type &&
            obj.getTarget().equals(target) &&
            obj.getAmount() == amount
        ).toList();
    }
    public List<QuestObjective> queryObjectives(ObjectiveType type, String target) {
        return objectives.stream()
        .filter(obj -> obj.getType() == type && obj.getTarget().equals(target)).toList();
    }
    public List<QuestObjective> queryObjectives(ObjectiveType type, int amount) {
        return objectives.stream()
        .filter(obj -> obj.getType() == type && obj.getAmount() == amount).toList();
    }
    public List<QuestObjective> queryObjectives(String target, int amount) {
        return objectives.stream()
        .filter(obj -> obj.getTarget().equals(target) && obj.getAmount() == amount).toList();
    }
    public List<QuestObjective> queryObjectives(ObjectiveType type) {
        return objectives.stream()
        .filter(obj -> obj.getType() == type).toList();
    }
    public List<QuestObjective> queryObjectives(int amount) {
        return objectives.stream()
        .filter(obj -> obj.getAmount() == amount).toList();
    }
    public List<QuestObjective> queryObjectives(String target) {
        return objectives.stream()
        .filter(obj -> obj.getTarget().equals(target)).toList();
    }

    public List<QuestReward> getRewards() {
        return List.copyOf(rewards);
    }

    public QuestData(
        String name, String displayName, String description, String npc,
        List<QuestObjective> objectives, List<QuestReward> rewards,
        long duration, long cooldown) {
        
        if(cooldown < duration) throw new IllegalArgumentException("Cooldown cannot be less than duration");
        
        this.cooldown = cooldown;
        this.duration = duration;
        this.displayName = displayName;
        this.name = name;
        this.description = description;
        this.npc = npc;
        this.objectives = objectives;
        this.rewards = rewards;

        List<Component> pages = new ArrayList<>(1);

        int i = 0;
        do {
            int endIndex = Math.min((i+1)*1000, description.length());
            String pageText = description.substring(i*1000, endIndex);
            pages.add(Component.text(pageText));
        } while(i++ < description.length() / 1000);

        if(((TextComponent)pages.getLast()).content().length() >= 500)
            pages.add(Component.text(""));
        pages.set(pages.size()-1,pages.getLast().append(Component.text("\n\nЦель:\n").decorate(TextDecoration.BOLD)));

        for(QuestObjective objective : objectives) {
            StringBuilder objectiveText = new StringBuilder().append("- ");
            switch(objective.getType()) {
                case ObjectiveType.GIVE  -> objectiveText.append("Принести ").append(objective.getAmount()).append(" ").append(objective.getTarget()).append("\n");
                case ObjectiveType.KILL  -> objectiveText.append("Убить ").append(objective.getAmount()).append(" ").append(objective.getTarget()).append("\n");
                case ObjectiveType.CRAFT -> objectiveText.append("Создать ").append(objective.getAmount()).append(" ").append(objective.getTarget()).append("\n");
                case ObjectiveType.SMELT -> objectiveText.append("Переплавить ").append(objective.getAmount()).append(" ").append(objective.getTarget()).append("\n");
                case ObjectiveType.BREW  -> objectiveText.append("Приготовить ").append(objective.getAmount()).append(" ").append(objective.getTarget()).append("\n");
            }
            pages.set(pages.size()-1,pages.getLast().append(Component.text(objectiveText.toString())));
        }
        pages.set(pages.size()-1,pages.getLast().append(Component.text("\nНаграда:\n").decorate(TextDecoration.BOLD)));
        for(QuestReward reward : rewards) {
            StringBuilder rewardText = new StringBuilder().append("- ");
            switch(reward.getType()) {
                case RewardType.EXP:
                    rewardText.append(reward.getAmount()).append(" опыта\n");
                    break;
                case RewardType.MONEY:
                    rewardText.append("$").append(reward.getAmount()).append("\n");
                    break;
                case RewardType.ITEM:
                    String itemName = Material.matchMaterial(reward.getIdentifier()).name();
                    rewardText.append(reward.getAmount()).append(" ").append(itemName).append("\n");
                    break;
            }
            pages.set(pages.size()-1,pages.getLast().append(Component.text(rewardText.toString())));
        }
        pages.set(pages.size()-1,pages.getLast().append(Component.text("Принять\n").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
        .clickEvent(ClickEvent.callback(audience -> {
            if(audience instanceof Player player) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "requests " + player.getName() + " " + name + " accept");
            }
        }))).append(Component.text("Отклонить\n").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)
        .clickEvent(ClickEvent.callback(audience -> {
            if(audience instanceof Player player) {
                player.closeInventory();
            }
        }))));

        BookMeta meta = (BookMeta) book.getItemMeta();

        meta.title(Component.text(displayName));
        meta.author(Component.text(npc));
        meta.pages(pages);

        book.setItemMeta(meta);
    }
}
