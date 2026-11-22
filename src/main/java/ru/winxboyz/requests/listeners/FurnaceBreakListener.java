package ru.winxboyz.requests.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import ru.winxboyz.requests.managers.InventoryManager;

public class FurnaceBreakListener implements Listener {
    
    private InventoryManager inventoryManager;

    @EventHandler(ignoreCancelled = true)
    public void onFurnaceBreak(BlockBreakEvent event) {
        inventoryManager.removeIssuer(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true)
    public void onFurnaceExplode(BlockExplodeEvent event) {
        inventoryManager.handleExplosion(event.blockList());
    }

    @EventHandler(ignoreCancelled = true)
    public void onFurnaceExplodeByEntity(EntityExplodeEvent event) {
        inventoryManager.handleExplosion(event.blockList());
    }

    public FurnaceBreakListener(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

}
