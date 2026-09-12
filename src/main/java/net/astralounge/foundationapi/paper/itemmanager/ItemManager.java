package net.astralounge.foundationapi.paper.itemmanager;

import net.astralounge.foundationapi.common.localisation.LocalisationService;
import net.astralounge.foundationapi.common.manager.CommonManager;
import net.astralounge.foundationapi.paper.manager.PaperManager;
import net.astralounge.foundationapi.paper.plugin.FoundationPaperPlugin;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unused")
public class ItemManager<T extends FoundationPaperPlugin<T, ?>>
        extends PaperManager<T>
        implements Listener {

    private final Map<String, CustomItem> customItemMap;
    private final NamespacedKey namespacedKey;
    private ItemFactory itemFactory;

    public ItemManager(T plugin) {
        super(plugin);
        customItemMap = new HashMap<>();
        namespacedKey = new NamespacedKey(plugin, "custom_item");
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @Override
    public void onDisable() {
        customItemMap.clear();

    }

    public ItemFactory getItemFactory() {
        if (itemFactory == null) {
            itemFactory = new ItemFactory(LocalisationService.getInstance().getTextCreator());
        }
        return itemFactory;
    }

    @Override
    public Collection<Class<? extends CommonManager>> getCommonDependencies() {
        return List.of();
    }

    public void registerCustomItem(CustomItem item) {
        if (customItemMap == null) throw new RuntimeException("CustomItemManager has not been initialized yet!");
        customItemMap.put(item.getClass().getSimpleName(), item);
        registerRecipes(item);
    }

    private void registerRecipes(CustomItem item) {
        for (Recipe recipe : item.getRecipes()) {
            Recipe bukkitRecipe = Bukkit.getRecipe(item.getNameSpace());
            if (bukkitRecipe == null) {
                Bukkit.addRecipe(recipe);
            } else if (!recipe.equals(bukkitRecipe)) {
                Bukkit.addRecipe(recipe);
            }
        }
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    public <CUSTOM_ITEM extends CustomItem> @Nullable CUSTOM_ITEM getCustomItem(Class<CUSTOM_ITEM> itemClazz) {
        CustomItem customItem = customItemMap.get(itemClazz.getSimpleName());
        if (customItem == null) return null;
        return itemClazz.cast(customItem);
    }

    private @Nullable String checkPersistentDataMatch(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }

        return meta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
    }

    private @Nullable CustomItem getCustomItemFromItemStack(ItemStack item) {
        String name = checkPersistentDataMatch(item);
        if (name == null) return null;

        return customItemMap.get(name);
    }

    @EventHandler
    private void onPlayerInteract(PlayerInteractEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getItem());
        if (item != null) item.onPlayerInteract(event);
    }

    @EventHandler
    private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getPlayer().getInventory().getItemInMainHand());
        if (item != null) item.onPlayerInteractEntity(event);
    }

    @EventHandler
    private void onConsumption(PlayerItemConsumeEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getItem());
        if (item != null) item.onConsumption(event);
    }

    @EventHandler
    private void onItemMerge(ItemMergeEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getEntity().getItemStack());
        if (item != null) item.onItemMerge(event);
    }

    @EventHandler
    private void onItemSpawn(ItemSpawnEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getEntity().getItemStack());
        if (item != null) item.onItemSpawn(event);
    }

    @EventHandler
    private void onItemDespawn(ItemDespawnEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getEntity().getItemStack());
        if (item != null) item.onItemDespawn(event);
    }

    @EventHandler
    private void onItemCraft(CraftItemEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getInventory().getResult());
        if (item != null) item.onItemCraft(event);
    }

    @EventHandler
    private void onItemSmith(SmithItemEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getInventory().getResult());
        if (item != null) item.onItemSmith(event);
    }

    @EventHandler
    private void onBlockDropItem(BlockDropItemEvent event) {
        List<CustomItem> items = event.getItems()
                .stream()
                .map(item -> getCustomItemFromItemStack(item.getItemStack()))
                .filter(Objects::nonNull)
                .toList();
        for (CustomItem item : items) {
            item.onBlockDropItem(event);
        }
    }

    @EventHandler
    private void onPlayerHeldItem(PlayerItemHeldEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getPlayer().getActiveItem());
        if (item != null) item.onPlayerHeldItem(event);
    }

    @EventHandler
    private void onPlayerItemMend(PlayerItemMendEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getItem());
        if (item != null) item.onPlayerItemMend(event);
    }

    @EventHandler
    private void onEnchantItem(EnchantItemEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getItem());
        if (item != null) item.onEnchantItem(event);
    }

    @EventHandler
    private void onEntityDropItem(EntityDropItemEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getItemDrop().getItemStack());
        if (item != null) item.onEntityDropItem(event);
    }

    @EventHandler
    private void onPlayerDropItem(PlayerDropItemEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getItemDrop().getItemStack());
        if (item != null) item.onPlayerDropItem(event);
    }

    @EventHandler
    private void onPlayerItemBreak(PlayerItemBreakEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getBrokenItem());
        if (item != null) item.onPlayerItemBreak(event);
    }

    @EventHandler
    private void onEntityPickupItem(EntityPickupItemEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getItem().getItemStack());
        if (item != null) item.onEntityPickupItem(event);
    }

    @EventHandler
    private void onPlayerItemDamage(PlayerItemDamageEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getItem());
        if (item != null) item.onPlayerItemDamage(event);
    }

    @EventHandler
    private void onCompostItem(CompostItemEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getItem());
        if (item != null) item.onCompostItem(event);
    }

    @EventHandler
    private void onPrepareItemCraft(PrepareItemCraftEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getInventory().getResult());
        if (item != null) item.onPrepareItemCraft(event);
    }

    @EventHandler
    private void onInventoryMoveItem(InventoryMoveItemEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getItem());
        if (item != null) item.onInventoryMoveItem(event);
    }

    @EventHandler
    private void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        CustomItem mainItem = getCustomItemFromItemStack(event.getMainHandItem());
        if (mainItem != null) mainItem.onPlayerSwapHandItems(event);

        CustomItem offHandItem = getCustomItemFromItemStack(event.getOffHandItem());
        if (offHandItem != null) offHandItem.onPlayerSwapHandItems(event);
    }

    @EventHandler
    private void onInventoryPickupItem(InventoryPickupItemEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getItem().getItemStack());
        if (item != null) item.onInventoryPickupItem(event);
    }

    @EventHandler
    private void onCartographyItem(CartographyItemEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getInventory().getResult());
        if (item != null) item.onCartographyItem(event);
    }

    @EventHandler
    private void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getItem());
        if (item != null) item.onPrepareItemEnchant(event);
    }

    @EventHandler
    private void onPlayerPickItem(PlayerPickItemEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getPlayer().getItemOnCursor());
        if (item != null) item.onPlayerPickItem(event);
    }

    @EventHandler
    private void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getItem().getItemStack());
        if (item != null) item.onPlayerAttemptPickupItem(event);
    }

    @EventHandler
    private void onEntityDamageItem(EntityDamageItemEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getItem());
        if (item != null) item.onEntityDamageItem(event);
    }

    @EventHandler
    private void onEntityCompostItem(EntityCompostItemEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getItem());
        if (item != null) item.onEntityCompostItem(event);
    }

    @EventHandler
    private void onPlayerItemCooldown(PlayerItemCooldownEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getPlayer().getActiveItem());
        if (item != null) item.onPlayerItemCooldown(event);
    }

    @EventHandler
    private void onPlayerStopUsingItem(PlayerStopUsingItemEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getItem());
        if (item != null) item.onPlayerStopUsingItem(event);
    }

    @EventHandler
    private void onPlayerItemFrameChange(PlayerItemFrameChangeEvent event) {
        CustomItem item = getCustomItemFromItemStack(event.getItemStack());
        if (item != null) item.onPlayerItemFrameChangeEvent(event);
    }
}
