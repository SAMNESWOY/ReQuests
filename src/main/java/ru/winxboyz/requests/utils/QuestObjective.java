package ru.winxboyz.requests.utils;

public class QuestObjective {
    private final ObjectiveType type;
    private final String target;
    private final int amount;

    public ObjectiveType getType() {
        return type;
    }
    public String getTarget() {
        return target;
    }
    public int getAmount() {
        return amount;
    }

    public QuestObjective(ObjectiveType type, String target, int amount) {
        this.type = type;
        this.target = target;
        this.amount = amount;
    }
}
