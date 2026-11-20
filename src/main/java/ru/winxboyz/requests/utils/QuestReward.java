package ru.winxboyz.requests.utils;

public class QuestReward {

    private final RewardType type;
    private final String identifier;
    private final int amount;

    public RewardType getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getAmount() {
        return amount;
    }

    public QuestReward(RewardType type, int amount) {
        this.type = type;
        this.identifier = null;
        this.amount = amount;
    }

    public QuestReward(RewardType type, String identifier, int amount) {
        this.type = type;
        this.identifier = identifier;
        this.amount = amount;
    }
}
