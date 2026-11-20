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
        if(
            questManager.npcHasObjectiveOf(npc, ObjectiveType.GIVE) &&
            questManager.hasQuestFrom(player, npc)) {
                questManager.trackGive(player, questManager.getItems(npc, ObjectiveType.GIVE));
        }
        questManager.openQuest(player, npc);
    }

    public NpcInteractListener(QuestManager npcManager) {
        this.questManager = npcManager;
    }

}
