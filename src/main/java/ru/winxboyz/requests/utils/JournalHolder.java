package ru.winxboyz.requests.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class JournalHolder implements InventoryHolder {

    // private final Player player;
    private final List<Inventory> inventories = new ArrayList<>();

    // public Player getPlayer() {
    //     return player;
    // }
    private int page = 1;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        if(page < 1 || page > inventories.size())
            return;
        this.page = page;
    }

    public void nextPage() {
        if(page < inventories.size())
            page++;
    }

    public void previousPage() {
        if(page > 1)
            page--;
    }

    public void addInventory(Inventory inventory) {
        if(inventory != null)
            inventories.add(inventory);
    }


    @Override
    public @NotNull Inventory getInventory() {
        return inventories.get(page-1);
    }

    public Inventory getInventory(int page) {
        if(page < 1 || page > inventories.size())
            return null;
        return inventories.get(page-1);
    }

    public JournalHolder() {}

    // public JournalHolder(Player player) {
    //     this.player = player;
    // }

}
