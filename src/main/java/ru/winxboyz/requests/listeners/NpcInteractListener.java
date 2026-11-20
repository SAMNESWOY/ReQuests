package ru.winxboyz.requests.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import ru.winxboyz.requests.managers.QuestManager;
import ru.winxboyz.requests.utils.ObjectiveType;

public class NpcInteractListener implements Listener {

    private final QuestManager questManager;

    @EventHandler
    public void onNpcInteract(NpcInteractEvent event) {
        String npc = event.getNpc().getData().getId();
        Player player = event.getPlayer();
        if(!questManager.isNpcQuestGiver(npc)) return;
        if(
            questManager.hasObjective(npc, ObjectiveType.GIVE) &&
            questManager.hasQuestFrom(player, npc)) {
                questManager.trackGive(player, questManager.getItems(npc, ObjectiveType.GIVE));
        } else if(!questManager.hasQuestFrom(player,npc))
            questManager.openQuest(player, npc);
    }

    public NpcInteractListener(QuestManager npcManager) {
        this.questManager = npcManager;
    }

}
