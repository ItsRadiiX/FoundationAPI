package net.astralounge.foundationapi.paper.itemmanager;

import net.astralounge.foundationapi.paper.itemmanager.definitions.AttributeModifierDefinition;
import net.astralounge.foundationapi.paper.itemmanager.definitions.EnchantmentDefinition;
import org.bukkit.*;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * High‑level, configuration‑friendly representation of an ItemStack.
 *
 * This is meant to be fully serializable to HOCON via Configurate,
 * and then turned into your runtime ItemStack (via ItemStackCreator).
 *
 * Fields are public-on-purpose to keep it POJO / data‑only.
 */
public final class ItemDefinition {

    /* -------------------------------------------------------------
     * Core identity / reuse
     * ------------------------------------------------------------- */

    public String id;

    /* -------------------------------------------------------------
     * Base ItemStack data
     * ------------------------------------------------------------- */

    /** Bukkit material of the item. */
    public Material material = Material.BARRIER;

    /** Amount in stack (default 1). */
    public int amount = 1;

    /** Optional custom model data */
    public Integer customModelData;

    /** Optional max stack size override (null = vanilla default). */
    public Integer maxStackSize;

    /** Optional rarity (some clients/versions use this). */
    public ItemRarity rarity;

    /** MiniMessage display name. */
    public String name;

    /** MiniMessage lore lines. */
    public List<String> lore;

    /** Whether the item is actually unbreakable. */
    public Boolean unbreakable;

    /** Force enchantment glint visually, independent of real enchants. */
    public Boolean enchantmentGlintOverride;

    /** Whether the item is fire‑resistant. */
    public Boolean fireResistant;

    /** Optional generic damage (for Damageable items). */
    public Integer damage;

    /* -------------------------------------------------------------
     * Enchantments & attributes & flags
     * ------------------------------------------------------------- */

    /** Normal enchantments. */
    public List<EnchantmentDefinition> enchantments;

    /** Stored enchantments (e.g. enchanted books). */
    public List<EnchantmentDefinition> storedEnchantments;

    /** Item flags like HIDE_ATTRIBUTES, HIDE_ENCHANTS, etc. */
    public List<ItemFlag> flags;

    /** Attribute modifiers (attack damage, armor, etc.) by attribute. */
    public List<AttributeModifierDefinition> attributes;

    /* -------------------------------------------------------------
     * Food / consumable
     * ------------------------------------------------------------- */

    /** Optional food data (hunger, saturation, etc). */
    public FoodDefinition food;

    /* -------------------------------------------------------------
     * Meta‑specific sections
     * Only relevant when the material supports that meta.
     * ------------------------------------------------------------- */

    /** Armor trim for armor items. */
    public ArmorTrimDefinition armorTrim;

    /** Armor stand item settings. */
    public ArmorStandDefinition armorStand;

    /** Axolotl bucket variant. */
    public AxolotlBucketDefinition axolotlBucket;

    /** Banner patterns. */
    public BannerDefinition banner;

    /** BlockData meta (string serialized blockdata). */
    public BlockDataDefinition blockData;

    /** BlockState meta (for container blocks; high‑level reference). */
    public BlockStateDefinition blockState;

    /** Book (written book) metadata. */
    public BookDefinition book;

    /** Bundle contents (recursively defined items). */
    public BundleDefinition bundle;

    /** Compass lodestone configuration. */
    public CompassDefinition compass;

    /** Crossbow charged projectiles. */
    public CrossbowDefinition crossbow;

    /** Single firework effect meta (for FireworkEffectMeta items). */
    public FireworkEffectDefinition fireworkEffect;

    /** Firework rocket metadata. */
    public FireworkMetaDefinition firework;

    /** Knowledge book recipes. */
    public KnowledgeBookDefinition knowledgeBook;

    /** Leather armor color. */
    public LeatherArmorDefinition leather;

    /** Map item data. */
    public MapDefinition map;

    /** Music instrument (goat horn). */
    public MusicInstrumentDefinition musicInstrument;

    /** Potion metadata. */
    public PotionDefinition potion;

    /** Repair cost for repairable items (e.g. anvil). */
    public Integer repairCost;

    /** Skull / head metadata. */
    public SkullDefinition skull;

    /** Spawn egg metadata. */
    public SpawnEggDefinition spawnEgg;

    /** Suspicious stew custom effects. */
    public SuspiciousStewDefinition suspiciousStew;

    /** Tropical fish bucket meta. */
    public TropicalFishBucketDefinition tropicalFishBucket;

    /* -------------------------------------------------------------
     * Catch‑all extension hook
     * ------------------------------------------------------------- */

    /** Arbitrary plugin‑specific data bag (optional). */
    public Map<String, Object> extra;

    /* ======================================================================
     * Nested definitions
     * ====================================================================== */

    /* -------------------- Food -------------------- */

    public static final class FoodDefinition {
        public int nutrition;                 // hunger points
        public float saturationModifier;      // saturation
        public boolean canAlwaysEat;          // like golden apples
        public float eatSeconds;              // optional, how long it takes
        public List<PotionEffectOverrideDefinition> effects; // e.g. golden carrot night vision, etc.
    }

    public static final class PotionEffectOverrideDefinition {
        public PotionEffectType type;
        public int durationTicks;
        public int amplifier;
        public boolean ambient;
        public boolean showParticles;
        public boolean showIcon;
        public double chance; // 0.0–1.0, for chance‑based food effects
    }

    /* -------------------- Armor Trim -------------------- */

    public static final class ArmorTrimDefinition {
        public TrimMaterial material;
        public TrimPattern pattern;
    }

    /* -------------------- Armor Stand -------------------- */

    public static final class ArmorStandDefinition {
        public Boolean invisible;
        public Boolean marker;
        public Boolean noBasePlate;
        public Boolean showArms;
        public Boolean small;
        // Pose could be added later if you want (head, body, arms, legs angles)
    }

    /* -------------------- Axolotl Bucket -------------------- */

    public static final class AxolotlBucketDefinition {
        public Axolotl.Variant variant;
    }

    /* -------------------- Banner -------------------- */

    public static final class BannerDefinition {
        public List<BannerPatternDefinition> patterns;
    }

    public static final class BannerPatternDefinition {
        public org.bukkit.block.banner.PatternType type;
        public DyeColor color;
    }

    /* -------------------- BlockData / BlockState -------------------- */

    public static final class BlockDataDefinition {
        /** Mojang/Bukkit blockdata string, e.g. "minecraft:oak_log[axis=y]" */
        public String dataString;
    }

    public static final class BlockStateDefinition {
        /**
         * High‑level reference to a block state.
         * For example: type + NBT‑like map, or maybe a location if you want to copy.
         * Keep it simple for now:
         */
        public Material blockType;
        public Map<String, Object> nbtLikeData; // optional structured data
    }

    /* -------------------- Book -------------------- */

    public static final class BookDefinition {
        public String title;
        public String author;
        public BookMeta.Generation generation;
        public List<String> pages; // MiniMessage pages
    }

    /* -------------------- Bundle -------------------- */

    public static final class BundleDefinition {
        /** Items stored inside the bundle. */
        public List<ItemDefinition> items;
    }

    /* -------------------- Compass -------------------- */

    public static final class CompassDefinition {
        public Boolean lodestoneTracked;  // whether lodestone is tracked
        public LodestoneLocation lodestone;
    }

    public static final class LodestoneLocation {
        public String worldName;  // world name, to be resolved at runtime
        public double x;
        public double y;
        public double z;
    }

    /* -------------------- Crossbow -------------------- */

    public static final class CrossbowDefinition {
        /** Projectiles the crossbow is pre‑charged with. */
        public List<ItemDefinition> chargedProjectiles;
    }

    /* -------------------- Firework Effect (single) -------------------- */

    public static final class FireworkEffectDefinition {
        public Boolean flicker;
        public Boolean trail;
        public FireworkEffect.Type type;
        public List<Color> colors;
        public List<Color> fadeColors;
    }

    /* -------------------- Firework Rocket -------------------- */

    public static final class FireworkMetaDefinition {
        public int power;  // rocket flight power
        public List<FireworkEffectDefinition> effects;
    }

    /* -------------------- Knowledge Book -------------------- */

    public static final class KnowledgeBookDefinition {
        /** Recipes as namespaced key strings: "minecraft:diamond_sword" etc. */
        public List<String> recipes;
    }

    /* -------------------- Leather Armor -------------------- */

    public static final class LeatherArmorDefinition {
        /** Leather color (Bukkit Color). */
        public Color color;
    }

    /* -------------------- Map -------------------- */

    public static final class MapDefinition {
        public Color color;
        public Boolean scaling;
        public Integer mapId;           // optional specific map id
        public String worldName;        // optional world for mapview
        public Integer xCenter;
        public Integer zCenter;
        public MapView.Scale scale;     // map zoom level
    }

    /* -------------------- Music Instrument (Goat Horn) -------------------- */

    public static final class MusicInstrumentDefinition {
        /** Namespaced key string for the instrument, e.g. "minecraft:ponder_goat_horn". */
        public String instrumentKey;
    }

    /* -------------------- Potion -------------------- */

    public static final class PotionDefinition {
        public PotionType baseType;
        public Boolean extended;
        public Boolean upgraded;
        public Color color;
        public List<PotionEffectOverrideDefinition> customEffects;
    }

    /* -------------------- Skull / Head -------------------- */

    public static final class SkullDefinition {
        public UUID ownerUuid;
        public String ownerName;
        public String skinUrl;                   // HTTP/texture URL
        public NamespacedKey noteBlockSound;     // sound when used as note block
    }

    /* -------------------- Spawn Egg -------------------- */

    public static final class SpawnEggDefinition {
        public EntityType customSpawnedType;
        // For full snapshots you could later add extra fields (nbtLikeData, etc.).
    }

    /* -------------------- Suspicious Stew -------------------- */

    public static final class SuspiciousStewDefinition {
        public List<SuspiciousEffectEntryDefinition> customEffects;
    }

    public static final class SuspiciousEffectEntryDefinition {
        public PotionEffectType type;
        public int durationTicks;
    }

    /* -------------------- Tropical Fish Bucket -------------------- */

    public static final class TropicalFishBucketDefinition {
        public DyeColor bodyColor;
        public DyeColor patternColor;
        public TropicalFish.Pattern pattern;
    }
}