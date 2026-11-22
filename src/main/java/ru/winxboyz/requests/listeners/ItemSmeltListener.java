package ru.winxboyz.requests.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ru.winxboyz.requests.managers.QuestManager;

public class ItemSmeltListener implements Listener {

    private QuestManager manager;

    private final Map<UUID,List<Inventory>> furnaces = new HashMap<>();

    public void ItemFurnacePutListener(InventoryInteractEvent event) {
        InventoryType type = event.getInventory().getType();
        if(!List.of(InventoryType.BLAST_FURNACE, InventoryType.FURNACE, InventoryType.SMOKER).contains(type))
            return;
        Player player = (Player) event.getWhoClicked();
        if(furnaces.get(player.getUniqueId()) == null)
            furnaces.put(player.getUniqueId(), new ArrayList<>());
        else
            furnaces.get(player.getUniqueId()).add(event.getInventory());
    }

    @EventHandler
    public void onItemSmelt(FurnaceSmeltEvent event) {
        ItemStack result = event.getResult();
        BlockState state = event.getBlock().getState();
        if(!(state instanceof Furnace furnace))
            return;
        try {
            List<Inventory> inventories = furnaces.values().stream()
            .filter(fl -> fl.contains(furnace.getInventory())).findFirst().get();

            UUID playerUUID = furnaces.keySet().stream().filter(p -> furnaces.get(p) == inventories).findFirst().get();
            
            manager.trackSmelt(Bukkit.getPlayer(playerUUID), result);
        } catch(NoSuchElementException e) {
            return;
        }
    }

    public ItemSmeltListener(QuestManager manager) {
        this.manager = manager;
    }

}
