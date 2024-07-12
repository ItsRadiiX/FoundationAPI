package com.itsradiix.foundationapi.paper.itemmanager;

import io.papermc.paper.event.block.CompostItemEvent;
import io.papermc.paper.event.entity.EntityCompostItemEvent;
import io.papermc.paper.event.entity.EntityDamageItemEvent;
import io.papermc.paper.event.player.*;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public abstract class CustomItem {
    private final ItemStack itemStack;

    abstract ItemStackCreator createItemData();
    abstract List<Recipe> getRecipes();

    public CustomItem() {
        ItemStackCreator itemStackCreator = createItemData();
        itemStackCreator.addPersistentData(
                ItemManager.getNamespacedKey(),
                PersistentDataType.STRING,
                getClass().getSimpleName());
        itemStack = itemStackCreator.result();
    }

    public ItemStack getCustomItem() {
        return itemStack;
    }

    /*
        Overridable methods for implementation of CustomItem
     */
    public void onInteract(PlayerInteractEvent event){}
    public void onConsumption(PlayerItemConsumeEvent event){}
    public void onItemMerge(ItemMergeEvent event){}
    public void onItemSpawn(ItemSpawnEvent event){}
    public void onItemDespawn(ItemDespawnEvent event){}
    public void onItemCraft(CraftItemEvent event){}
    public void onItemSmith(SmithItemEvent event){}
    public void onBlockDropItem(BlockDropItemEvent event){}
    public void onPlayerHeldItem(PlayerItemHeldEvent event){}
    public void onPlayerItemMend(PlayerItemMendEvent event){}
    public void onEnchantItem(EnchantItemEvent event){}
    public void onEntityDropItem(EntityDropItemEvent event){}
    public void onPlayerDropItem(PlayerDropItemEvent event){}
    public void onPlayerItemBreak(PlayerItemBreakEvent event){}
    public void onEntityPickupItem(EntityPickupItemEvent event){}
    public void onPlayerItemDamage(PlayerItemDamageEvent event){}
    public void onCompostItem(CompostItemEvent event){}
    public void onPrepareItemCraft(PrepareItemCraftEvent event){}
    public void onInventoryMoveItem(InventoryMoveItemEvent event){}
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event){}
    public void onInventoryPickupItem(InventoryPickupItemEvent event){}
    public void onCartographyItem(CartographyItemEvent event){}
    public void onPrepareItemEnchant(PrepareItemEnchantEvent event){}
    public void onPlayerPickItem(PlayerPickItemEvent event){}
    public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent event){}
    public void onEntityDamageItem(EntityDamageItemEvent event){}
    public void onEntityCompostItem(EntityCompostItemEvent event){}
    public void onPlayerItemCooldown(PlayerItemCooldownEvent event){}
    public void onPlayerStopUsingItem(PlayerStopUsingItemEvent event){}
    public void onPlayerItemFrameChangeEvent(PlayerItemFrameChangeEvent event){}




}
