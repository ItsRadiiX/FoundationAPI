package net.astralounge.foundationapi.paper.menumanager.v1.menuitems;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MenuItemIcon {
    // Basic Data
    public String material = "NAME_TAG";
    public int customModelData = -1;
    public boolean enchanted = false;

    // Player Head Data
    public @Nullable UUID skullOwnerUUID;

    public MenuItemIcon() {}

//    /**
//     * Bridges the raw data to your functional ItemStackCreator.
//     */
//    public @NotNull ItemStackCreator createCreator() {
//        Material mat = Material.matchMaterial(material);
//        if (mat == null) mat = Material.BARRIER;
//
//        ItemStackCreator creator = new ItemStackCreator(mat)
//                .setCustomModelData(customModelData)
//                .setEnchantmentGlintOverride(enchanted)
//                .addItemFlags(ItemFlag.values());
//
//        if (skullOwnerUUID != null) {
//            creator.setSkullOwnerUUID(skullOwnerUUID);
//        }
//
//        return creator;
//    }
}
