package net.astralounge.foundationapi.paper.itemmanager.serializers;

import net.astralounge.foundationapi.paper.itemmanager.ItemDefinition.FoodDefinition;
import net.astralounge.foundationapi.paper.itemmanager.ItemDefinition.PotionEffectOverrideDefinition;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class FoodDefinitionSerializer implements TypeSerializer<FoodDefinition> {

    @Override
    public FoodDefinition deserialize(Type type, ConfigurationNode node) throws SerializationException {
        if (node.virtual()) return null;

        FoodDefinition food = new FoodDefinition();
        food.nutrition = node.node("nutrition").getInt(0);
        food.saturationModifier = (float) node.node("saturation").getDouble(0.0);
        food.canAlwaysEat = node.node("can-always-eat").getBoolean(false);
        food.eatSeconds = (float) node.node("eat-seconds").getDouble(0.0);

        List<PotionEffectOverrideDefinition> effects = new ArrayList<>();
        for (ConfigurationNode child : node.node("effects").childrenList()) {
            String typeName = child.node("type").getString();
            if (typeName == null) continue;

            PotionEffectType effectType = PotionEffectType.getByName(typeName.toUpperCase(Locale.ROOT));
            if (effectType == null) {
                throw new SerializationException("Unknown potion effect type: " + typeName);
            }

            PotionEffectOverrideDefinition o = new PotionEffectOverrideDefinition();
            o.type = effectType;
            o.durationTicks = child.node("duration").getInt(0);
            o.amplifier = child.node("amplifier").getInt(0);
            o.ambient = child.node("ambient").getBoolean(false);
            o.showParticles = child.node("show-particles").getBoolean(true);
            o.showIcon = child.node("show-icon").getBoolean(true);
            o.chance = child.node("chance").getDouble(1.0);
            effects.add(o);
        }
        food.effects = effects.isEmpty() ? null : effects;

        // If everything is default/empty, just treat as null
        if (food.nutrition == 0 &&
                food.saturationModifier == 0.0f &&
                !food.canAlwaysEat &&
                food.eatSeconds == 0.0f &&
                (food.effects == null || food.effects.isEmpty())) {
            return null;
        }

        return food;
    }

    @Override
    public void serialize(@NotNull Type type, @Nullable FoodDefinition food, @NotNull ConfigurationNode node)
            throws SerializationException {

        if (food == null) {
            node.set(null);
            return;
        }

        if (food.nutrition != 0) node.node("nutrition").set(food.nutrition);
        if (food.saturationModifier != 0.0f) node.node("saturation").set(food.saturationModifier);
        if (food.canAlwaysEat) node.node("can-always-eat").set(true);
        if (food.eatSeconds != 0.0f) node.node("eat-seconds").set(food.eatSeconds);

        if (food.effects != null && !food.effects.isEmpty()) {
            ConfigurationNode list = node.node("effects");
            for (PotionEffectOverrideDefinition o : food.effects) {
                if (o == null || o.type == null) continue;
                ConfigurationNode c = list.appendListNode();
                c.node("type").set(o.type.getName());
                if (o.durationTicks != 0) c.node("duration").set(o.durationTicks);
                if (o.amplifier != 0) c.node("amplifier").set(o.amplifier);
                if (o.ambient) c.node("ambient").set(true);
                if (!o.showParticles) c.node("show-particles").set(false);
                if (!o.showIcon) c.node("show-icon").set(false);
                if (o.chance != 1.0) c.node("chance").set(o.chance);
            }
        }
    }
}