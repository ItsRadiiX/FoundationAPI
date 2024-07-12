package com.itsradiix.foundationapi.paper.itemmanager;

import com.destroystokyo.paper.inventory.meta.ArmorStandMeta;
import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.potion.SuspiciousEffectEntry;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Utility class for creating custom ItemStacks with specific metadata.
 * {@code @Author} ItsRadiiX Aka Bryan Suk
 */

@SuppressWarnings({"unused", "", "UnstableApiUsage"})
public class ItemStackCreator {

    private final ItemStack result;
    private final ItemMeta itemMeta;

    /**
     * Constructor to create an ItemCreator instance with the specified material.
     *
     * @param material The material of the ItemStack to be created.
     */
    public ItemStackCreator(Material material){
        result = new ItemStack(material);
        itemMeta = result.getItemMeta();
    }

    /**
     * Retrieves the created ItemStack with the applied metadata.
     *
     * @return The ItemStack with applied metadata.
     */
    public ItemStack result(){
        result.setItemMeta(itemMeta);
        return result;
    }

    /*
            ItemMeta Data
     */

    /**
     * Sets the display name of the ItemStack.
     *
     * @param component The display name component.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setName(Component component){
        if (component == null) return this;
        itemMeta.displayName(component);
        return this;
    }

    /**
     * Sets the display name of the ItemStack.
     *
     * @param name The display name component.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setName(String name){
        if (name == null || name.isBlank()) return this;
        itemMeta.displayName(Component.text(name));
        return this;
    }

    /**
     * Sets the amount of items in the ItemStack.
     *
     * @param amount The amount of items.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setAmount(int amount){
        result.setAmount(amount);
        return this;
    }

    /**
     * Sets the lore of the ItemStack.
     *
     * @param components The list of lore components.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setLore(List<Component> components){
        if (components == null) return this;

        itemMeta.lore(components);
        return this;
    }

    /**
     * Sets the lore of the ItemStack.
     *
     * @param components The lore components.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setLore(Component... components){
        if (components == null) return this;

        itemMeta.lore(Arrays.asList(components));
        return this;
    }

    /**
     * Sets the lore of the ItemStack.
     *
     * @param lore The lore strings.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setLore(String... lore){
        if (lore == null || lore.length == 0) return this;

        itemMeta.lore();
        return this;
    }

    /**
     * Adds an enchantment to the ItemStack.
     *
     * @param enchantment The enchantment to add.
     * @param level The level of the enchantment.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator addEnchantment(Enchantment enchantment, int level){
        if (enchantment == null) return this;

        itemMeta.addEnchant(enchantment, level, true);
        return this;
    }

    /**
     * Adds multiple enchantments to the ItemStack.
     *
     * @param enchantments The map of enchantments to add.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator addEnchantments(Map<Enchantment, Integer> enchantments){
        if (enchantments == null) return this;
        if (enchantments.isEmpty()) return this;

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()){
            itemMeta.addEnchant(entry.getKey(), entry.getValue(), true);
        }

        return this;
    }

    /**
     * Adds an attribute to the ItemStack.
     *
     * @param attribute The attribute to add.
     * @param modifier The modifier of the attribute.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator addAttribute(Attribute attribute, AttributeModifier modifier){
        if (attribute == null || modifier == null) return this;

        itemMeta.addAttributeModifier(attribute, modifier);
        return this;
    }

    /**
     * Adds multiple attributes to the ItemStack.
     *
     * @param attributes The map of attributes to add.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator addAttributes(Map<Attribute, AttributeModifier> attributes){
        if (attributes == null) return this;
        if (attributes.isEmpty()) return this;

        for (Map.Entry<Attribute, AttributeModifier> entry : attributes.entrySet()){
            itemMeta.addAttributeModifier(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Adds an item flag to the ItemStack.
     *
     * @param itemFlag The item flag to add.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator addItemFlag(ItemFlag itemFlag){
        if (itemFlag == null) return this;

        itemMeta.addItemFlags(itemFlag);
        return this;
    }

    /**
     * Adds multiple item flags to the ItemStack.
     *
     * @param itemFlags The item flags to add.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator addItemFlags(ItemFlag... itemFlags){
        if (itemFlags == null) return this;
        if (itemFlags.length == 0) return this;

        itemMeta.addItemFlags(itemFlags);
        return this;
    }

    /**
     * Adds multiple item flags to the ItemStack.
     *
     * @param itemFlags The item flags to add.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator addItemFlags(List<ItemFlag> itemFlags){
        if (itemFlags == null) return this;
        if (itemFlags.isEmpty()) return this;

        for (ItemFlag itemFlag : itemFlags){
            itemMeta.addItemFlags(itemFlag);
        }
        return this;
    }

    /**
     * Sets the unbreakable state of the ItemStack.
     *
     * @param condition The unbreakable condition.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator isUnbreakable(boolean condition){
        itemMeta.setUnbreakable(condition);
        return this;
    }

    /**
     * Sets the custom model data of the ItemStack.
     *
     * @param customModelData The custom model data.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setCustomModelData(int customModelData){
        if (customModelData == -1) return this;

        itemMeta.setCustomModelData(customModelData);
        return this;
    }

    /**
     * Adds persistent data to the ItemStack.
     *
     * @param namespacedKey The namespaced key.
     * @param dataType The persistent data type.
     * @param object The persistent data object.
     * @param <K> The type of the persistent data key.
     * @param <V> The type of the persistent data value.
     * @return The ItemCreator instance.
     */
    public <K, V> ItemStackCreator addPersistentData(NamespacedKey namespacedKey, PersistentDataType<K, V> dataType, V object){
        if (namespacedKey == null || dataType == null || object == null) return this;
        if (namespacedKey.getKey().isEmpty() || namespacedKey.getKey().isBlank()) return this;

        itemMeta.getPersistentDataContainer().set(namespacedKey, dataType, object);
        return this;
    }

    /**
     * Adds persistent data to the ItemStack.
     *
     * @param key The key.
     * @param dataType The persistent data type.
     * @param object The persistent data object.
     * @param <K> The type of the persistent data key.
     * @param <V> The type of the persistent data value.
     * @return The ItemCreator instance.
     */
    public <K, V> ItemStackCreator addPersistentData(JavaPlugin plugin, String key, PersistentDataType<K, V> dataType, V object){
        if (key == null || dataType == null || object == null) return this;
        if (key.isEmpty() || key.isBlank()) return this;

        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, key), dataType, object);
        return this;
    }

    /**
     * Adds persistent data to the ItemStack.
     *
     * @param key The key.
     * @param dataType The persistent data type.
     * @param object The persistent data object.
     * @param <K> The type of the persistent data key.
     * @param <V> The type of the persistent data value.
     * @return The ItemCreator instance.
     */
    public <K, V> ItemStackCreator addPersistentData(String key, PersistentDataType<K, V> dataType, V object){
        if (key == null || dataType == null || object == null) return this;
        if (key.isEmpty() || key.isBlank()) return this;

        itemMeta.getPersistentDataContainer().set(new NamespacedKey(ItemManager.getPlugin(), key), dataType, object);
        return this;
    }


    /*
            Regular Item meta
     */

    public ItemStackCreator setEnchantmentGlintOverride(boolean override){
        itemMeta.setEnchantmentGlintOverride(override);
        return this;
    }

    public ItemStackCreator setFireResistant(boolean resistant){
        itemMeta.setFireResistant(resistant);
        return this;
    }

    public ItemStackCreator setFood(FoodComponent food){
        itemMeta.setFood(food);
        return this;
    }

    public ItemStackCreator setMaxStackSize(int maxStackSize){
        itemMeta.setMaxStackSize(maxStackSize);
        return this;
    }

    public ItemStackCreator setRarity(ItemRarity rarity){
        itemMeta.setRarity(rarity);
        return this;
    }

    public ItemStackCreator setUnbreakable(boolean unbreakable){
        itemMeta.setUnbreakable(unbreakable);
        return this;
    }

    /*
            Damageable Item
     */

    /**
     * Sets the durability of the ItemStack if it's a damageable item.
     *
     * @param durability The durability value.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setDurability(short durability){
        if (itemMeta instanceof Damageable d){
            d.setDamage(durability);
        }
        return this;
    }

    /*
            ArmorMeta Item
     */

    /**
     * Sets the trim of the armor if it's an armor item.
     *
     * @param armorTrim The armor trim.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setArmorTrim(ArmorTrim armorTrim){
        if (itemMeta instanceof ArmorMeta armorMeta){
            armorMeta.setTrim(armorTrim);
        }
        return this;
    }

    /*
            ArmorStand Item
     */

    /**
     * Sets the invisibility state of the armor stand if it's an armor stand item.
     *
     * @param invisible The invisibility state.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setArmorStandInvisible(boolean invisible){
        if (itemMeta instanceof ArmorStandMeta armorStandMeta){
            armorStandMeta.setInvisible(invisible);
        }
        return this;
    }

    /**
     * Sets the marker state of the armor stand if it's an armor stand item.
     *
     * @param marker The marker state.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setArmorStandMarker(boolean marker){
        if (itemMeta instanceof ArmorStandMeta armorStandMeta){
            armorStandMeta.setMarker(marker);
        }
        return this;
    }

    /**
     * Sets the baseplate state of the armor stand if it's an armor stand item.
     *
     * @param noBasePlate The baseplate state.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setArmorStandNoBasePlate(boolean noBasePlate){
        if (itemMeta instanceof ArmorStandMeta armorStandMeta){
            armorStandMeta.setNoBasePlate(noBasePlate);
        }
        return this;
    }

    /**
     * Sets the show arms state of the armor stand if it's an armor stand item.
     *
     * @param showArms The show arms state.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setArmorStandShowArms(boolean showArms){
        if (itemMeta instanceof ArmorStandMeta armorStandMeta){
            armorStandMeta.setShowArms(showArms);
        }
        return this;
    }

    /**
     * Sets the small state of the armor stand if it's an armor stand item.
     *
     * @param small The small state.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setArmorStandSmall(boolean small){
        if (itemMeta instanceof ArmorStandMeta armorStandMeta){
            armorStandMeta.setSmall(small);
        }
        return this;
    }

    /*
            AxolotlBucket item
     */

    /**
     * Sets the variant of the axolotl if it's an axolotl bucket item.
     *
     * @param variant The axolotl variant.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setAxolotlVariant(Axolotl.Variant variant){
        if (itemMeta instanceof AxolotlBucketMeta meta){
            meta.setVariant(variant);
        }
        return this;
    }

    /*
            Banner Item
     */

    /**
     * Sets the patterns of the banner if it's a banner item.
     *
     * @param patterns The list of banner patterns.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setBannerPatterns(List<Pattern> patterns){
        if (itemMeta instanceof BannerMeta meta){
            meta.setPatterns(patterns);
        }
        return this;
    }

    /**
     * Sets a specific pattern at the given index of the banner if it's a banner item.
     *
     * @param index The index of the pattern.
     * @param pattern The banner pattern.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setBannerPattern(int index, Pattern pattern){
        if (itemMeta instanceof BannerMeta meta){
            meta.setPattern(index,pattern);
        }
        return this;
    }

    /*
            BlockData item
     */

    /**
     * Sets the block data of the item if it's a block data item.
     *
     * @param data The block data.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setBlockData(BlockData data){
        if (itemMeta instanceof BlockDataMeta meta){
            meta.setBlockData(data);
        }
        return this;
    }

    /*
            BlockState item
     */

    /**
     * Sets the block state of the item if it's a block state item.
     *
     * @param blockState The block state.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setBlockState(BlockState blockState){
        if (itemMeta instanceof BlockStateMeta meta){
            meta.setBlockState(blockState);
        }
        return this;
    }

    /*
            Book Item
     */

    /**
     * Sets the pages of the book if it's a book item.
     *
     * @param pages The pages of the book.
     * @return The ItemCreator instance.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public ItemStackCreator setBookPages(Component... pages){
        if (itemMeta instanceof BookMeta meta) {
            meta.pages(pages);
        }
        return this;
    }

    /**
     * Sets a specific page of the book at the given index if it's a book item.
     *
     * @param index The index of the page.
     * @param page The page component.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setBookPage(int index, Component page){
        if (itemMeta instanceof BookMeta meta) {
            meta.page(index, page);
        }
        return this;
    }

    /**
     * Sets the author of the book if it's a book item.
     *
     * @param author The author component.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setBookAuthor(Component author){
        if (itemMeta instanceof BookMeta meta) {
            meta.author(author);
        }
        return this;
    }

    /**
     * Sets the title of the book if it's a book item.
     *
     * @param title The title component.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setBookTitle(Component title){
        if (itemMeta instanceof BookMeta meta) {
            meta.title(title);
        }
        return this;
    }

    /**
     * Sets the generation of the book if it's a book item.
     *
     * @param generation The book generation.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setBookGeneration(BookMeta.Generation generation){
        if (itemMeta instanceof BookMeta meta) {
            meta.setGeneration(generation);
        }
        return this;
    }

    /*
            Bundle Item
            EXPERIMENTAL FEATURE
     */

    /**
     * Sets the items inside the bundle if it's a bundle item.
     *
     * @param items The list of items.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setBundleItems(List<ItemStack> items){
        if (itemMeta instanceof BundleMeta meta){
            meta.setItems(items);
        }
        return this;
    }

    /*
            Compass item
     */

    /**
     * Sets the lodestone location for the compass if it's a compass item.
     *
     * @param lodestone The lodestone location.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setCompassLodestone(Location lodestone){
        if (itemMeta instanceof CompassMeta meta){
            meta.setLodestone(lodestone);
        }
        return this;
    }

    /**
     * Sets whether the lodestone of the compass should be tracked if it's a compass item.
     *
     * @param tracked The tracked state.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setCompassLodestoneTracked(boolean tracked){
        if (itemMeta instanceof CompassMeta meta){
            meta.setLodestoneTracked(tracked);
        }
        return this;
    }

    /*
        Crossbow Item
     */

    /**
     * Sets the charged projectiles for the crossbow if it's a crossbow item.
     *
     * @param projectiles The list of charged projectiles.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setCrossbowChargedProjectiles(List<ItemStack> projectiles){
        if (itemMeta instanceof CrossbowMeta meta){
            meta.setChargedProjectiles(projectiles);
        }
        return this;
    }

    /**
     * Adds a charged projectile to the crossbow if it's a crossbow item.
     *
     * @param projectile The charged projectile.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator addCrossbowChargedProjectile(ItemStack projectile){
        if (itemMeta instanceof CrossbowMeta meta){
            meta.addChargedProjectile(projectile);
        }
        return this;
    }

    /*
            Stored Enchantment Item
     */

    /**
     * Adds a stored enchantment to the item if it's an enchantment storage item.
     *
     * @param enchantment The enchantment to add.
     * @param level The level of the enchantment.
     * @param ignoreLevelRestriction Whether to ignore the level restriction.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator addStoredEnchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction){
        if (itemMeta instanceof EnchantmentStorageMeta meta){
            meta.addStoredEnchant(enchantment, level, ignoreLevelRestriction);
        }
        return this;
    }


    /*
            FireworkEffect Item
     */

    /**
     * Sets the firework effect for the firework effect item.
     *
     * @param effect The firework effect.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setFireworkSingleEffect(FireworkEffect effect){
        if (itemMeta instanceof  FireworkEffectMeta meta){
            meta.setEffect(effect);
        }
        return this;
    }

    /*
            Firework item
     */

    /**
     * Adds a firework effect to the firework item.
     *
     * @param effect The firework effect to add.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator addFireworkEffect(FireworkEffect effect){
        if (itemMeta instanceof  FireworkMeta meta){
            meta.addEffect(effect);
        }
        return this;
    }

    /**
     * Adds multiple firework effects to the firework item.
     *
     * @param effects The array of firework effects to add.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator addFireworkEffects(FireworkEffect... effects){
        if (itemMeta instanceof FireworkMeta meta){
            meta.addEffects(effects);
        }
        return this;
    }

    /**
     * Adds multiple firework effects to the firework item.
     *
     * @param effects The iterable of firework effects to add.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator addFireworkEffects(Iterable<FireworkEffect> effects){
        if (itemMeta instanceof FireworkMeta meta){
            meta.addEffects(effects);
        }
        return this;
    }

    /**
     * Sets the power of the firework item.
     *
     * @param power The power of the firework.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator addFireworkPower(int power){
        if (itemMeta instanceof FireworkMeta meta){
            meta.setPower(power);
        }
        return this;
    }

    /*
            Knowledge book item
     */

    /**
     * Sets the recipes for the knowledge book item.
     *
     * @param recipes The list of recipes.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setKnowledgeBookRecipes(List<NamespacedKey> recipes){
        if (itemMeta instanceof KnowledgeBookMeta meta){
            meta.setRecipes(recipes);
        }
        return this;
    }

    /**
     * Adds recipes to the knowledge book item.
     *
     * @param recipes The recipes to add.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator addKnowledgeBookRecipes(NamespacedKey... recipes){
        if (itemMeta instanceof KnowledgeBookMeta meta){
            meta.addRecipe(recipes);
        }
        return this;
    }

    /*
        Leather armor item
     */

    /**
     * Sets the color of the leather armor if it's a leather armor item.
     *
     * @param color The color of the armor.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setLeatherColor(Color color){
        if (itemMeta instanceof LeatherArmorMeta meta){
            meta.setColor(color);
        }
        return this;
    }

    /*
            Map item
     */

    /**
     * Sets the color of the map if it's a map item.
     *
     * @param color The color of the map.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setMapColor(Color color){
        if (itemMeta instanceof MapMeta meta){
            meta.setColor(color);
        }
        return this;
    }

    /**
     * Sets the map view of the map item.
     *
     * @param map The map view.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setMapView(MapView map){
        if (itemMeta instanceof MapMeta meta){
            meta.setMapView(map);
        }
        return this;
    }

    /**
     * Sets whether the map is scaling if it's a map item.
     *
     * @param scaling The scaling state of the map.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setMapScaling(boolean scaling){
        if (itemMeta instanceof MapMeta meta){
            meta.setScaling(scaling);
        }
        return this;
    }

    /*
            MusicInstrument Item (goat horn)
     */

    /**
     * Sets the instrument of the music instrument item.
     *
     * @param instrument The music instrument.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setInstrument(MusicInstrument instrument){
        if (itemMeta instanceof MusicInstrumentMeta meta){
            meta.setInstrument(instrument);
        }
        return this;
    }

    /*
            Potion Item
     */

    /**
     * Sets the base potion type of the potion item.
     *
     * @param type The base potion type.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setBasePotionType(PotionType type){
        if (itemMeta instanceof PotionMeta meta){
            meta.setBasePotionType(type);
        }
        return this;
    }

    /**
     * Sets the color of the potion item.
     *
     * @param color The color of the potion.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setPotionColor(Color color){
        if (itemMeta instanceof PotionMeta meta){
            meta.setColor(color);
        }
        return this;
    }

    /**
     * Adds a custom potion effect to the potion item.
     *
     * @param effect The custom potion effect.
     * @param overwrite Whether to overwrite existing effects.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator addCustomPotionEffect(PotionEffect effect, boolean overwrite){
        if (itemMeta instanceof PotionMeta meta){
            meta.addCustomEffect(effect, overwrite);
        }
        return this;
    }

    /*
            Repairable Item
     */

    /**
     * Sets the repair cost of the repairable item.
     *
     * @param cost The repair cost.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setRepairCost(int cost){
        if (itemMeta instanceof Repairable meta){
            meta.setRepairCost(cost);
        }
        return this;
    }

    /*
            Skull Item
     */

    /**
     * Sets the player profile for the skull item.
     *
     * @param profile The player profile.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setSkullPlayerProfile(PlayerProfile profile){
        if (itemMeta instanceof SkullMeta meta){
            meta.setPlayerProfile(profile);
        }
        return this;
    }

    public ItemStackCreator setSkullOwningPlayer(OfflinePlayer owner){
        if (itemMeta instanceof SkullMeta meta){
            meta.setOwningPlayer(owner);
        }
        return this;
    }

    public ItemStackCreator setSkullNoteBlockSound(NamespacedKey noteBlockSound){
        if (itemMeta instanceof SkullMeta meta){
            meta.setNoteBlockSound(noteBlockSound);
        }
        return this;
    }

    /*
            Spawn egg Item
     */

    /**
     * Sets the custom spawned entity type for the spawn egg item.
     *
     * @param type The custom spawned entity type.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setCustomSpawnedType(EntityType type){
        if (itemMeta instanceof SpawnEggMeta meta){
            meta.setCustomSpawnedType(type);
        }
        return this;
    }

    public ItemStackCreator setSpawnedEntity(EntitySnapshot snapshot){
        if (itemMeta instanceof SpawnEggMeta meta){
            meta.setSpawnedEntity(snapshot);
        }
        return this;
    }

    /*
            Suspicious stew item
     */

    /**
     * Adds a custom effect to the suspicious stew item.
     *
     * @param effect The custom effect to add.
     * @param overwrite Whether to overwrite existing effects.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator addSuspiciousStewCustomEffect(SuspiciousEffectEntry effect, boolean overwrite){
        if (itemMeta instanceof SuspiciousStewMeta meta){
            meta.addCustomEffect(effect, overwrite);
        }
        return this;
    }

    /*
            Tropical Fish Bucket item
     */

    /**
     * Sets the body color of the tropical fish if it's a tropical fish bucket item.
     *
     * @param color The body color of the tropical fish.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setFishBodyColor(DyeColor color){
        if (itemMeta instanceof TropicalFishBucketMeta meta){
            meta.setBodyColor(color);
        }
        return this;
    }

    /**
     * Sets the pattern of the tropical fish if it's a tropical fish bucket item.
     *
     * @param pattern The pattern of the tropical fish.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setFishPattern(TropicalFish.Pattern pattern){
        if (itemMeta instanceof TropicalFishBucketMeta meta){
            meta.setPattern(pattern);
        }
        return this;
    }

    /**
     * Sets the pattern color of the tropical fish if it's a tropical fish bucket item.
     *
     * @param color The pattern color of the tropical fish.
     * @return The ItemCreator instance.
     */
    public ItemStackCreator setFishPatternColor(DyeColor color){
        if (itemMeta instanceof TropicalFishBucketMeta meta){
            meta.setPatternColor(color);
        }
        return this;
    }
}
