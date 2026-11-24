package ru.winxboyz.requests.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.oliver.fancynpcs.api.events.NpcInteractEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import ru.winxboyz.requests.managers.QuestManager;
import ru.winxboyz.requests.utils.ObjectiveType;
import ru.winxboyz.requests.utils.QuestData;
import ru.winxboyz.requests.utils.TimeMessage;

public class NpcInteractListener implements Listener {

    private final QuestManager questManager;

    @EventHandler
    public void onNpcInteract(NpcInteractEvent event) {
        // // У NPC два идентификатора: внутренний id и имя. Квест ищем по обоим.
        // QuestData quest = questManager.getQuestByNpc(
        //     event.getNpc().getData().getId(),
        //     event.getNpc().getData().getName()
        // );
        // if(quest == null) return;

        String npcName = event.getNpc().getData().getName();
        String npcId   = event.getNpc().getData().getId();
        


        if(!questManager.isNpcQuestGiver(npcName, npcId)) return;
        QuestData quest = questManager.getQuest(npcName, npcId);
        if(quest == null)
            quest = questManager.getQuest(npcName, npcId);
        
        Player player = event.getPlayer();
        if(questManager.isOnCooldown(player, quest)) {
            player.sendActionBar(
                Component.text("Квест станет доступен через ").color(NamedTextColor.RED)
                .append(Component.text(TimeMessage.toString(questManager.getCooldown(player, quest), "ru")).color(NamedTextColor.AQUA)));
            return;
        }
        if(
            questManager.hasObjective(quest, ObjectiveType.GIVE) &&
            questManager.hasQuestFrom(player, npcName, npcId)) {
                questManager.trackGive(player, player.getActiveItem(), npcName, npcId);
        } else if(!questManager.hasQuestFrom(player, npcName, npcId))
            questManager.openQuest(player, npcName, npcId);
        else
            player.sendActionBar(Component.text("Квест уже взят").color(NamedTextColor.RED));
    }

    public NpcInteractListener(QuestManager npcManager) {
        this.questManager = npcManager;
    }

}
