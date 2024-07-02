package nl.bryansuk.foundationapi.paper.itemmanager;

import com.google.common.collect.Multimap;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("unused")
public class ItemMetaComparator {

    public static boolean containsAllMeta(ItemStack itemA, ItemStack itemB) {
        ItemMeta metaA = itemA.getItemMeta();
        ItemMeta metaB = itemB.getItemMeta();

        return containsAllMeta(metaA, metaB);
    }

    public static boolean containsAllMeta(ItemMeta itemA, ItemMeta itemB){

        boolean compareDisplayName = compareDisplayName(itemA, itemB);
        boolean compareLore = compareLore(itemA, itemB);
        boolean enchants = containsAllEnchantments(itemA, itemB);
        boolean itemFlags = containsAllItemFlags(itemA, itemB);
        boolean attributes = containsAllAttributes(itemA, itemB);
        boolean modelData = containsCustomModelData(itemA, itemB);
        boolean persistentData = equalsPersistentData(itemA, itemB);
        boolean customModelData = equalsCustomModelData(itemA, itemB);

        return compareDisplayName
                && compareLore
                && enchants
                && itemFlags
                && attributes
                && modelData
                && persistentData
                && customModelData;
    }

    public static boolean compareDisplayName(ItemMeta metaA, ItemMeta metaB){
        if (metaB.hasDisplayName()){
            return Objects.equals(metaB.displayName(), metaA.displayName());
        }
        return true;
    }

    public static boolean compareLore(ItemMeta metaA, ItemMeta metaB){
        if (metaB.hasLore()){
            return Objects.equals(metaB.lore(), metaA.lore());
        }
        return true;
    }

    public static boolean containsCustomModelData(ItemMeta metaA, ItemMeta metaB){
        // Check Custom Model Data
        if (metaB.hasCustomModelData()) {
            if (!metaA.hasCustomModelData()) return false;
            return metaB.getCustomModelData() == metaA.getCustomModelData();
        }
        return true;
    }

    public static boolean equalsCustomModelData(ItemMeta metaA, ItemMeta metaB){

        if (!metaA.hasCustomModelData() || !metaB.hasCustomModelData()) return false;

        int dataA = metaA.getCustomModelData();
        int dataB = metaB.getCustomModelData();

        return dataA == dataB;
    }

    public static boolean containsAllEnchantments(ItemMeta metaA, ItemMeta metaB) {
        Map<Enchantment, Integer> enchantmentsB = metaB.getEnchants();
        for (Map.Entry<Enchantment, Integer> entry : enchantmentsB.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int levelB = entry.getValue();

            if (!metaA.hasEnchant(enchantment)) {
                return false;
            }

            int levelA = metaA.getEnchantLevel(enchantment);
            if (levelA < levelB) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsAllAttributes(ItemMeta metaA, ItemMeta metaB){
        Multimap<Attribute, AttributeModifier> mapA = metaA.getAttributeModifiers();
        Multimap<Attribute, AttributeModifier> mapB = metaB.getAttributeModifiers();

        if (isEmptyOrNull(mapA) && isEmptyOrNull(mapB)) return true;

        if (isEmptyOrNull(mapA)) return false;
        if (isEmptyOrNull(mapB)) return true;

        for (Map.Entry<Attribute, AttributeModifier> entry : mapB.entries()){
            if(!mapA.containsEntry(entry.getKey(), entry.getValue())) return false;
        }

        return true;
    }

    private static boolean isEmptyOrNull(Multimap<?, ?> multimap) {
        return multimap == null || multimap.isEmpty();
    }

    public static boolean containsAllItemFlags(ItemMeta metaA, ItemMeta metaB) {
        Set<ItemFlag> flagsB = metaB.getItemFlags();
        for (ItemFlag flag : flagsB) {
            if (!metaA.hasItemFlag(flag)) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalsPersistentData(ItemMeta metaA, ItemMeta metaB){
        // Persistent Data
        if (!metaB.getPersistentDataContainer().isEmpty()) {
            for (NamespacedKey key : metaB.getPersistentDataContainer().getKeys()) {
                if (!metaA.getPersistentDataContainer().has(key)) return false;
            }
        }
        return true;
    }
}
