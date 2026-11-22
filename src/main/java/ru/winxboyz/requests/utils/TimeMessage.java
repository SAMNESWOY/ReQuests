package ru.winxboyz.requests.utils;

public class TimeMessage {
    private static final String
        SECONDS_RU = "секунд",
        SECONDS_EN = "second",
        MINUTES_RU = "минут",
        MINUTES_EN = "minute",
        HOURS_RU   = "час",
        HOURS_EN   = "hour";
    
    
    private static String timeMessage(String timeUnit, int amount) {
        switch (timeUnit) {
            case SECONDS_RU:
            case MINUTES_RU:
                if(amount % 10 == 1 && amount % 100 != 11)
                    return amount + " " + timeUnit + "у";
                else if(1 < amount % 10 && amount % 10 < 5 && (amount % 100) / 10 != 1)
                    return amount + " " + timeUnit + "ы";
                return amount + " " + timeUnit;
            case HOURS_RU:
                if(amount % 10 == 1 && amount % 100 != 11)
                    return amount + " " + timeUnit;
                else if(1 < amount % 10 && amount % 10 < 5 && (amount % 100) / 10 != 1)
                    return amount + " " + timeUnit + "а";
                return amount + " " + timeUnit + "ов";
            case SECONDS_EN:
            case MINUTES_EN:
            case HOURS_EN  :
                return amount + " " + timeUnit + (amount > 1 ? "s" : "");
            default:
                throw new IllegalArgumentException();
        }
    }

    public static String toString(int time, String locale) {
        int hours = time / 3600,
            minutes = (time/60) % 60,
            seconds = time % 60;

        return (hours > 0   ? timeMessage(locale.equals("ru") ? HOURS_RU : HOURS_EN, hours) + " " : "")
            +  (minutes > 0 ? timeMessage(locale.equals("ru") ? MINUTES_RU : MINUTES_EN, minutes) + " " : "")
            +  (seconds > 0 ? timeMessage(locale.equals("ru") ? SECONDS_RU : SECONDS_EN, seconds) : "");
    }
}
