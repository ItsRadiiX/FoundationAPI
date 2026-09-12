package net.astralounge.foundationapi.paper.itemmanager.serializers;

import net.astralounge.foundationapi.paper.itemmanager.ItemDefinition;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class ArmorStandDefinitionSerializer
        implements TypeSerializer<ItemDefinition.ArmorStandDefinition> {

    @Override
    public ItemDefinition.ArmorStandDefinition deserialize(
            Type type,
            ConfigurationNode node
    ) throws SerializationException {

        // Whole section missing/null => null
        if (node.virtual() || node.isNull()) {
            return null;
        }

        ItemDefinition.ArmorStandDefinition def = new ItemDefinition.ArmorStandDefinition();

        ConfigurationNode invisibleNode = node.node("invisible");
        if (!invisibleNode.virtual() && !invisibleNode.isNull()) {
            def.invisible = invisibleNode.getBoolean();
        }

        ConfigurationNode markerNode = node.node("marker");
        if (!markerNode.virtual() && !markerNode.isNull()) {
            def.marker = markerNode.getBoolean();
        }

        ConfigurationNode noBasePlateNode = node.node("no-base-plate");
        if (!noBasePlateNode.virtual() && !noBasePlateNode.isNull()) {
            def.noBasePlate = noBasePlateNode.getBoolean();
        }

        ConfigurationNode showArmsNode = node.node("show-arms");
        if (!showArmsNode.virtual() && !showArmsNode.isNull()) {
            def.showArms = showArmsNode.getBoolean();
        }

        ConfigurationNode smallNode = node.node("small");
        if (!smallNode.virtual() && !smallNode.isNull()) {
            def.small = smallNode.getBoolean();
        }

        return def;
    }

    @Override
    public void serialize(
            Type type, ItemDefinition.ArmorStandDefinition def,
            ConfigurationNode node
    ) throws SerializationException {

        if (def == null) {
            node.set(null);
            return;
        }

        if (def.invisible != null) {
            node.node("invisible").set(def.invisible);
        }

        if (def.marker != null) {
            node.node("marker").set(def.marker);
        }

        if (def.noBasePlate != null) {
            node.node("no-base-plate").set(def.noBasePlate);
        }

        if (def.showArms != null) {
            node.node("show-arms").set(def.showArms);
        }

        if (def.small != null) {
            node.node("small").set(def.small);
        }
    }
}