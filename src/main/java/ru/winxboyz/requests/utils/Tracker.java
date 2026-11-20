package ru.winxboyz.requests.utils;

public class Tracker {
    private final QuestData quest;
    private long startTime;
    private Long cooldownStartTime = null;
    private final int[] progress;

    public QuestData getQuest() {
        return quest;
    }

    public long getStartTime() {
        return startTime;
    }

    public Long getCooldownStartTime() {
        return cooldownStartTime;
    }

    public Long getResetAt() {
        return cooldownStartTime == null ? null : cooldownStartTime+quest.getCooldown();
    }

    public int[] getProgress() {
        return progress;
    }

    public boolean isOnCooldown() {
        return cooldownStartTime != null && System.currentTimeMillis() <= getResetAt();
    }

    public void sendOnCooldown() {
        cooldownStartTime = System.currentTimeMillis();
    }

    public Tracker(QuestData quest, long startTime) {
        this.quest = quest;
        progress = new int[quest.getObjectives().size()];
        for(int i = 0; i < progress.length; i++)
            progress[i] = 0;
        this.startTime = startTime;
    }
}
