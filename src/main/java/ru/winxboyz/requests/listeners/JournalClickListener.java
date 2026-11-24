package ru.winxboyz.requests.listeners;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import ru.winxboyz.requests.utils.JournalHolder;

public class JournalClickListener implements Listener {

    @EventHandler
    public void onJournalClick(InventoryClickEvent event) {
        HumanEntity player = event.getWhoClicked();
        if(player == null)
            return;
        if(player.getOpenInventory() == null)
            return;
        Inventory topInventory = player.getOpenInventory().getTopInventory();
        if(topInventory == null)
            return;

        if(
            topInventory.getHolder() == null ||
            !(topInventory.getHolder() instanceof JournalHolder journal))
            return;
        event.setCancelled(true);
        int slot = event.getRawSlot();
        if(slot == 18 || slot == 26) {
            if(slot == 26)
                journal.nextPage();
            else
                journal.previousPage();
            
            ((Player) event.getWhoClicked()).openInventory(journal.getInventory());
        }
    }

    public JournalClickListener() {}

}
