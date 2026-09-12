package net.astralounge.foundationapi.paper.itemmanager.serializers;

import net.astralounge.foundationapi.paper.datamanagement.persistance.material.MaterialSerializer;
import net.astralounge.foundationapi.paper.itemmanager.ItemDefinition;
import org.bukkit.Material;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public final class ItemSerializerBundle {

    public static void registerAll(TypeSerializerCollection.Builder builder) {
        builder.register(Material.class, new MaterialSerializer());

        builder.register(ItemDefinition.class, new ItemDefinitionSerializer());
    }
}
