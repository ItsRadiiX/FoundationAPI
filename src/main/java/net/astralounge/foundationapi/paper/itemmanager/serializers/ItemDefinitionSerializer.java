package net.astralounge.foundationapi.paper.itemmanager.serializers;

import net.astralounge.foundationapi.paper.itemmanager.ItemDefinition;
import net.astralounge.foundationapi.paper.itemmanager.definitions.AttributeModifierDefinition;
import net.astralounge.foundationapi.paper.itemmanager.definitions.EnchantmentDefinition;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

public final class ItemDefinitionSerializer implements TypeSerializer<ItemDefinition> {

    @Override
    public ItemDefinition deserialize(Type type, ConfigurationNode node) throws SerializationException {
        ItemDefinition def = new ItemDefinition();

        // Basic identity
        def.id = node.node("id").getString();

        // Material (optional override)
        ConfigurationNode matNode = node.node("material");
//        if (!matNode.virtual()) {
//            def.material = materialSerializer.deserialize(Material.class, matNode);
//        }

        def.amount = node.node("amount").getInt(def.amount);
        def.customModelData = node.node("custom-model-data").getInt(def.customModelData);

        if (!node.node("max-stack-size").virtual()) {
            def.maxStackSize = node.node("max-stack-size").getInt();
        }

        String rarityRaw = node.node("rarity").getString();
        if (rarityRaw != null) {
            def.rarity = ItemRarity.valueOf(rarityRaw.toUpperCase(Locale.ROOT));
        }

        def.name = node.node("name").getString();
        def.lore = node.node("lore").getList(String.class);

        if (!node.node("unbreakable").virtual()) {
            def.unbreakable = node.node("unbreakable").getBoolean();
        }
        if (!node.node("enchantment-glint-override").virtual()) {
            def.enchantmentGlintOverride = node.node("enchantment-glint-override").getBoolean();
        }
        if (!node.node("fire-resistant").virtual()) {
            def.fireResistant = node.node("fire-resistant").getBoolean();
        }
        if (!node.node("damage").virtual()) {
            def.damage = node.node("damage").getInt();
        }

        // Enchants, flags, attributes are separate types with their own serializers
        def.enchantments = node.node("enchantments").getList(EnchantmentDefinition.class);
        def.storedEnchantments = node.node("stored-enchantments").getList(EnchantmentDefinition.class);
        def.flags = node.node("flags").getList(ItemFlag.class);
        def.attributes = node.node("attributes").getList(AttributeModifierDefinition.class);

        // Food (nested)
        def.food = node.node("food").get(ItemDefinition.FoodDefinition.class);

        // Meta-specific nested objects – all delegated
        def.armorTrim = node.node("armor-trim").get(ItemDefinition.ArmorTrimDefinition.class);
        def.armorStand = node.node("armor-stand").get(ItemDefinition.ArmorStandDefinition.class);
        def.axolotlBucket = node.node("axolotl-bucket").get(ItemDefinition.AxolotlBucketDefinition.class);
        def.banner = node.node("banner").get(ItemDefinition.BannerDefinition.class);
        def.blockData = node.node("block-data").get(ItemDefinition.BlockDataDefinition.class);
        def.blockState = node.node("block-state").get(ItemDefinition.BlockStateDefinition.class);
        def.book = node.node("book").get(ItemDefinition.BookDefinition.class);
        def.bundle = node.node("bundle").get(ItemDefinition.BundleDefinition.class);
        def.compass = node.node("compass").get(ItemDefinition.CompassDefinition.class);
        def.crossbow = node.node("crossbow").get(ItemDefinition.CrossbowDefinition.class);
        def.fireworkEffect = node.node("firework-effect").get(ItemDefinition.FireworkEffectDefinition.class);
        def.firework = node.node("firework").get(ItemDefinition.FireworkMetaDefinition.class);
        def.knowledgeBook = node.node("knowledge-book").get(ItemDefinition.KnowledgeBookDefinition.class);
        def.leather = node.node("leather").get(ItemDefinition.LeatherArmorDefinition.class);
        def.map = node.node("map").get(ItemDefinition.MapDefinition.class);
        def.musicInstrument = node.node("music-instrument").get(ItemDefinition.MusicInstrumentDefinition.class);
        def.potion = node.node("potion").get(ItemDefinition.PotionDefinition.class);

        if (!node.node("repair-cost").virtual()) {
            def.repairCost = node.node("repair-cost").getInt();
        }

        def.skull = node.node("skull").get(ItemDefinition.SkullDefinition.class);
        def.spawnEgg = node.node("spawn-egg").get(ItemDefinition.SpawnEggDefinition.class);
        def.suspiciousStew = node.node("suspicious-stew").get(ItemDefinition.SuspiciousStewDefinition.class);
        def.tropicalFishBucket = node.node("tropical-fish-bucket").get(ItemDefinition.TropicalFishBucketDefinition.class);

//        // Extra map (raw object tree)
//        if (!node.node("extra").virtual()) {
//            def.extra = node.node("extra").get(List.class);
//        }

        return def;
    }

    @Override
    public void serialize(Type type, @Nullable ItemDefinition def, ConfigurationNode node)
            throws SerializationException {

        if (def == null) {
            node.set(null);
            return;
        }

        setIfNotNull(node, "id", def.id);

        // Material is required for a valid item; always write
        //materialSerializer.serialize(Material.class, def.material, node.node("material"));

        if (def.amount != 1) node.node("amount").set(def.amount);
        if (def.customModelData != -1) node.node("custom-model-data").set(def.customModelData);
        setIfNotNull(node, "max-stack-size", def.maxStackSize);
        setIfNotNull(node, "rarity", def.rarity);

        setIfNotNull(node, "name", def.name);
        setIfNotEmpty(node, "lore", def.lore);

        setIfNotNull(node, "unbreakable", def.unbreakable);
        setIfNotNull(node, "enchantment-glint-override", def.enchantmentGlintOverride);
        setIfNotNull(node, "fire-resistant", def.fireResistant);
        setIfNotNull(node, "damage", def.damage);

        setIfNotEmpty(node, "enchantments", def.enchantments);
        setIfNotEmpty(node, "stored-enchantments", def.storedEnchantments);
        setIfNotEmpty(node, "flags", def.flags);
        setIfNotEmpty(node, "attributes", def.attributes);

        // Nested objects – let their own serializers decide what to emit
        setIfNotNull(node.node("food"), def.food);

        setIfNotNull(node.node("armor-trim"), def.armorTrim);
        setIfNotNull(node.node("armor-stand"), def.armorStand);
        setIfNotNull(node.node("axolotl-bucket"), def.axolotlBucket);
        setIfNotNull(node.node("banner"), def.banner);
        setIfNotNull(node.node("block-data"), def.blockData);
        setIfNotNull(node.node("block-state"), def.blockState);
        setIfNotNull(node.node("book"), def.book);
        setIfNotNull(node.node("bundle"), def.bundle);
        setIfNotNull(node.node("compass"), def.compass);
        setIfNotNull(node.node("crossbow"), def.crossbow);
        setIfNotNull(node.node("firework-effect"), def.fireworkEffect);
        setIfNotNull(node.node("firework"), def.firework);
        setIfNotNull(node.node("knowledge-book"), def.knowledgeBook);
        setIfNotNull(node.node("leather"), def.leather);
        setIfNotNull(node.node("map"), def.map);
        setIfNotNull(node.node("music-instrument"), def.musicInstrument);
        setIfNotNull(node.node("potion"), def.potion);
        setIfNotNull(node, "repair-cost", def.repairCost);
        setIfNotNull(node.node("skull"), def.skull);
        setIfNotNull(node.node("spawn-egg"), def.spawnEgg);
        setIfNotNull(node.node("suspicious-stew"), def.suspiciousStew);
        setIfNotNull(node.node("tropical-fish-bucket"), def.tropicalFishBucket);

        if (def.extra != null && !def.extra.isEmpty()) {
            node.node("extra").set(def.extra);
        }
    }

    private void setIfNotNull(ConfigurationNode parent, String key, @Nullable Object value)
            throws SerializationException {
        if (value != null) parent.node(key).set(value);
    }

    private void setIfNotNull(ConfigurationNode node, @Nullable Object value)
            throws SerializationException {
        if (value != null) node.set(value);
    }

    private <T> void setIfNotEmpty(ConfigurationNode parent, String key, @Nullable List<T> list)
            throws SerializationException {
        if (list != null && !list.isEmpty()) {
            parent.node(key).set(list);
        }
    }
}