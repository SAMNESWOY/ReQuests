package ru.winxboyz.requests.utils;

public class Tracker {
    private QuestData quest;
    private long startTime;
    private final int[] progress;

    public QuestData getQuest() {
        return quest;
    }

    public long getStartTime() {
        return startTime;
    }

    public int[] getProgress() {
        return progress;
    }

    public Tracker(QuestData quest, long startTime) {
        this.quest = quest;
        progress = new int[quest.getObjectives().size()];
        for(int i = 0; i < progress.length; i++)
            progress[i] = 0;
        this.startTime = startTime;
    }
}
