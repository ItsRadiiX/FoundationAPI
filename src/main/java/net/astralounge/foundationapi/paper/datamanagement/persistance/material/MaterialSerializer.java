package net.astralounge.foundationapi.paper.datamanagement.persistance.material;

import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class MaterialSerializer implements TypeSerializer<Material> {

    @Override
    public Material deserialize(@NonNull Type type, ConfigurationNode node)
            throws SerializationException {
        String name = node.getString();
        if (name == null) {
            throw new SerializationException("Material must be a string");
        }

        Material mat = Material.matchMaterial(name);
        if (mat == null) {
            throw new SerializationException("Unknown material: " + name);
        }
        return mat;
    }

    @Override
    public void serialize(@NonNull Type type, @Nullable Material obj, ConfigurationNode node)
            throws SerializationException {
        node.set(obj == null ? null : obj.name());
    }
}
