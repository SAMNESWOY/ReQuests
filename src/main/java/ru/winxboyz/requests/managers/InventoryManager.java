package ru.winxboyz.requests.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import ru.winxboyz.requests.ReQuests;

public class InventoryManager {
    
    private final Map<Block, UUID> furnaces = new HashMap<>();

    private final ReQuests plugin;

    public void handleExplosion(List<Block> blocks) {
        for(Block block : blocks)
            if(furnaces.containsKey(block))
                removeIssuer(block);
    }

    public void handleShiftClick(Player player, ItemStack item) {
        if(item == null || item.getType() == Material.AIR)
            return;
        Bukkit.getScheduler().runTask(plugin, () -> {
            if(player == null)
                return;
            Inventory inventory = player.getOpenInventory().getTopInventory();

            if(inventory == null || inventory.getLocation() == null)
                return;

            Block block = inventory.getLocation().getBlock();

            if(!(block.getState() instanceof Furnace))
                return;

            ItemStack[] contents = inventory.getContents();
            if(contents[0] == null)
                furnaces.remove(block);
            else if(item.getType() == contents[0].getType())
                furnaces.put(block, player.getUniqueId());
        });
    }

    public void handleClick(Block block, Player player, int slot, ItemStack cursor, ItemStack current) {
        if(!(block.getState() instanceof Furnace))
            return;
        if(slot != 0)
            return;
        if(current != null && (cursor == null || cursor.getType() == Material.AIR))
            furnaces.remove(block);
        else if(cursor != null && cursor.getType() != Material.AIR)
            furnaces.put(block, player.getUniqueId());
    }

    public boolean hasIssuer(Block block) {
        return furnaces.containsKey(block);
    }

    public UUID getIssuer(Block block) {
        return furnaces.get(block);
    }

    public void removeIssuer(Block block, UUID uuid) {
        furnaces.remove(block, uuid);
    }

    public void removeIssuer(Block block) {
        furnaces.remove(block);
    }

    public InventoryManager(ReQuests plugin) {
        this.plugin = plugin;
    }

}
