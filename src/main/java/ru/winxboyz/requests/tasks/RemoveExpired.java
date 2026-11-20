package ru.winxboyz.requests.tasks;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import ru.winxboyz.requests.utils.Tracker;

public class RemoveExpired {

    private final ConcurrentHashMap<UUID,List<Tracker>> progress;

    public void tick() {
        if(progress.isEmpty()) return;
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<UUID,List<Tracker>>> iterator = progress.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<UUID,List<Tracker>> entry = iterator.next();
            List<Tracker> trackers = entry.getValue();
            Iterator<Tracker> iter = trackers.iterator();
            while(iter.hasNext()) {
                Tracker tracker = iter.next();
                if(!tracker.isOnCooldown() && currentTime >= tracker.getStartTime() + tracker.getQuest().getDuration()) {
                    iter.remove();
                }
            }
            // if(trackers.size() == 0)
            //     iterator.remove();
        }
    }

    public RemoveExpired(ConcurrentHashMap<UUID,List<Tracker>> progress) {
        this.progress = progress;
    }
}
