package ru.winxboyz.requests.utils;

public class Tracker {
    private final QuestData quest;
    private long questStartTime, cooldownStartTime;
    private final int[] progress;

    public QuestData getQuest() {
        return quest;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getCooldownStartTime() {
        return cooldownStartTime;
    }

    public long getResetAt() {
        return cooldownStartTime+quest.getCooldown();
    }

    public int[] getProgress() {
        return progress;
    }

    public Tracker(QuestData quest, long questStartTime, long cooldownStartTime) {
        this.quest = quest;
        progress = new int[quest.getObjectives().size()];
        for(int i = 0; i < progress.length; i++)
            progress[i] = 0;
        this.questStartTime = startTime;
        this.cooldownStartTime = cooldownStartTime;
    }
}
