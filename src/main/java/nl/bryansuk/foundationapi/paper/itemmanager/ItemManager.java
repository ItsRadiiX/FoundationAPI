package nl.bryansuk.foundationapi.paper.itemmanager;

import io.papermc.paper.event.block.CompostItemEvent;
import io.papermc.paper.event.entity.EntityCompostItemEvent;
import io.papermc.paper.event.entity.EntityDamageItemEvent;
import io.papermc.paper.event.player.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ItemManager implements Listener{

    private static Map<String, CustomItem> customItemMap;
    private static NamespacedKey namespacedKey;
    private static JavaPlugin plugin;

    public ItemManager(JavaPlugin plugin) {
        customItemMap = new HashMap<>();
        namespacedKey = new NamespacedKey(plugin, "custom_item");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        ItemManager.plugin = plugin;
    }

    public void registerCustomItem(CustomItem item){
        if(customItemMap == null) throw new RuntimeException("CustomItemManager has not been initialized yet!");
        customItemMap.put(item.getClass().getSimpleName(), item);
        registerRecipes(item);
    }

    private void registerRecipes(CustomItem item) {
        for (Recipe recipe : item.getRecipes()) {
            Bukkit.addRecipe(recipe);
        }
    }

    public static NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    private @Nullable String checkPersistentDataMatch(ItemStack item){
        if (item == null) return null;
        if (item.getItemMeta() == null) return null;

        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(namespacedKey, PersistentDataType.STRING);
    }

    private @Nullable CustomItem getCustomItem(ItemStack item) {
        String name = checkPersistentDataMatch(item);
        if (name == null) return null;

        return customItemMap.get(name);
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        CustomItem item = getCustomItem(event.getItem());
        if (item != null) item.onInteract(event);
    }

    @EventHandler
    private void onConsumption(PlayerItemConsumeEvent event) {
        CustomItem item = getCustomItem(event.getItem());
        if (item != null) item.onConsumption(event);
    }

    @EventHandler
    private void onItemMerge(ItemMergeEvent event) {
        CustomItem item = getCustomItem(event.getEntity().getItemStack());
        if (item != null) item.onItemMerge(event);
    }

    @EventHandler
    private void onItemSpawn(ItemSpawnEvent event) {
        CustomItem item = getCustomItem(event.getEntity().getItemStack());
        if (item != null) item.onItemSpawn(event);
    }

    @EventHandler
    private void onItemDespawn(ItemDespawnEvent event) {
        CustomItem item = getCustomItem(event.getEntity().getItemStack());
        if (item != null) item.onItemDespawn(event);
    }

    @EventHandler
    private void onItemCraft(CraftItemEvent event) {
        CustomItem item = getCustomItem(event.getInventory().getResult());
        if (item != null) item.onItemCraft(event);
    }

    @EventHandler
    private void onItemSmith(SmithItemEvent event) {
        CustomItem item = getCustomItem(event.getInventory().getResult());
        if (item != null) item.onItemSmith(event);
    }

    @EventHandler
    private void onBlockDropItem(BlockDropItemEvent event) {
        List<CustomItem> items = event.getItems()
                .stream()
                .map(item -> getCustomItem(item.getItemStack()))
                .toList();
        for (CustomItem item : items) {
            item.onBlockDropItem(event);
        }
    }

    @EventHandler
    private void onPlayerHeldItem(PlayerItemHeldEvent event) {
        CustomItem item = getCustomItem(event.getPlayer().getActiveItem());
        if (item != null) item.onPlayerHeldItem(event);
    }

    @EventHandler
    private void onPlayerItemMend(PlayerItemMendEvent event) {
        CustomItem item = getCustomItem(event.getItem());
        if (item != null) item.onPlayerItemMend(event);
    }

    @EventHandler
    private void onEnchantItem(EnchantItemEvent event) {
        CustomItem item = getCustomItem(event.getItem());
        if (item != null) item.onEnchantItem(event);
    }

    @EventHandler
    private void onEntityDropItem(EntityDropItemEvent event) {
        CustomItem item = getCustomItem(event.getItemDrop().getItemStack());
        if (item != null) item.onEntityDropItem(event);
    }

    @EventHandler
    private void onPlayerDropItem(PlayerDropItemEvent event) {
        CustomItem item = getCustomItem(event.getItemDrop().getItemStack());
        if (item != null) item.onPlayerDropItem(event);
    }

    @EventHandler
    private void onPlayerItemBreak(PlayerItemBreakEvent event) {
        CustomItem item = getCustomItem(event.getBrokenItem());
        if (item != null) item.onPlayerItemBreak(event);
    }

    @EventHandler
    private void onEntityPickupItem(EntityPickupItemEvent event) {
        CustomItem item = getCustomItem(event.getItem().getItemStack());
        if (item != null) item.onEntityPickupItem(event);
    }

    @EventHandler
    private void onPlayerItemDamage(PlayerItemDamageEvent event) {
        CustomItem item = getCustomItem(event.getItem());
        if (item != null) item.onPlayerItemDamage(event);
    }

    @EventHandler
    private void onCompostItem(CompostItemEvent event) {
        CustomItem item = getCustomItem(event.getItem());
        if (item != null) item.onCompostItem(event);
    }

    @EventHandler
    private void onPrepareItemCraft(PrepareItemCraftEvent event) {
        CustomItem item = getCustomItem(event.getInventory().getResult());
        if (item != null) item.onPrepareItemCraft(event);
    }

    @EventHandler
    private void onInventoryMoveItem(InventoryMoveItemEvent event) {
        CustomItem item = getCustomItem(event.getItem());
        if (item != null) item.onInventoryMoveItem(event);
    }

    @EventHandler
    private void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        CustomItem mainItem = getCustomItem(event.getMainHandItem());
        if (mainItem != null) mainItem.onPlayerSwapHandItems(event);

        CustomItem offHandItem = getCustomItem(event.getOffHandItem());
        if (offHandItem != null) offHandItem.onPlayerSwapHandItems(event);
    }

    @EventHandler
    private void onInventoryPickupItem(InventoryPickupItemEvent event) {
        CustomItem item = getCustomItem(event.getItem().getItemStack());
        if (item != null) item.onInventoryPickupItem(event);
    }

    @EventHandler
    private void onCartographyItem(CartographyItemEvent event) {
        CustomItem item = getCustomItem(event.getInventory().getResult());
        if (item != null) item.onCartographyItem(event);
    }

    @EventHandler
    private void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        CustomItem item = getCustomItem(event.getItem());
        if (item != null) item.onPrepareItemEnchant(event);
    }

    @EventHandler
    private void onPlayerPickItem(PlayerPickItemEvent event) {
        CustomItem item = getCustomItem(event.getPlayer().getItemOnCursor());
        if (item != null) item.onPlayerPickItem(event);
    }

    @EventHandler
    private void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent event) {
        CustomItem item = getCustomItem(event.getItem().getItemStack());
        if (item != null) item.onPlayerAttemptPickupItem(event);
    }
    @EventHandler
    private void onEntityDamageItem(EntityDamageItemEvent event) {
        CustomItem item = getCustomItem(event.getItem());
        if (item != null) item.onEntityDamageItem(event);
    }

    @EventHandler
    private void onEntityCompostItem(EntityCompostItemEvent event) {
        CustomItem item = getCustomItem(event.getItem());
        if (item != null) item.onEntityCompostItem(event);
    }

    @EventHandler
    private void onPlayerItemCooldown(PlayerItemCooldownEvent event) {
        CustomItem item = getCustomItem(event.getPlayer().getActiveItem());
        if (item != null) item.onPlayerItemCooldown(event);
    }

    @EventHandler
    private void onPlayerStopUsingItem(PlayerStopUsingItemEvent event) {
        CustomItem item = getCustomItem(event.getItem());
        if (item != null) item.onPlayerStopUsingItem(event);
    }

    @EventHandler
    private void onPlayerItemFrameChange(PlayerItemFrameChangeEvent event) {
        CustomItem item = getCustomItem(event.getItemStack());
        if (item != null) item.onPlayerItemFrameChangeEvent(event);
    }
}
