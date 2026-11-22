package ru.winxboyz.requests.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;

public class PageSwitcher extends ItemStack {
    private boolean isNextPage;

    public boolean getIsNextPage() {
        return isNextPage;
    }
    public PageSwitcher(boolean isNextPage) {
        super(Material.ACACIA_BUTTON);
        this.isNextPage = isNextPage;
        this.editMeta(meta -> meta.displayName(Component.text((isNextPage ? "Следующая" : "Предыдущая") + " страница")));
    }
}
