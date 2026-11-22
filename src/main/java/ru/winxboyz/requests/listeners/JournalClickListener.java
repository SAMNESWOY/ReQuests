package ru.winxboyz.requests.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import ru.winxboyz.requests.utils.JournalHolder;
import ru.winxboyz.requests.utils.PageSwitcher;

public class JournalClickListener implements Listener {

    @EventHandler
    public void onJournalClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if(clickedInventory == null || !(clickedInventory instanceof JournalHolder journal))
            return;
        event.setCancelled(true);

        if(event.getCurrentItem() instanceof PageSwitcher switcher) {
            if(switcher.getIsNextPage())
                journal.nextPage();
            else
                journal.previousPage();
            
            ((Player) event.getWhoClicked()).openInventory(journal.getInventory(journal.getPage()));
        }
    }

    public JournalClickListener() {}

}
