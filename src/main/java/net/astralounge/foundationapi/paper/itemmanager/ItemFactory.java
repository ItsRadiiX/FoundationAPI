package net.astralounge.foundationapi.paper.itemmanager;

import net.astralounge.foundationapi.common.localisation.TextCreator;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ItemFactory {

    private final TextCreator textCreator;

    public ItemFactory(TextCreator textCreator) {
        this.textCreator = textCreator;
    }

    public @NotNull ItemStackCreator builder(@NotNull Material material) {
        return new ItemStackCreator(textCreator, material);
    }
}
