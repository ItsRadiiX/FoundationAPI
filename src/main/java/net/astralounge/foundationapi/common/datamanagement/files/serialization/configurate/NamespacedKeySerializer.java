package net.astralounge.foundationapi.common.datamanagement.files.serialization.configurate;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class NamespacedKeySerializer
        implements TypeSerializer<NamespacedKey> {

    @Override
    public NamespacedKey deserialize(@NotNull Type type, ConfigurationNode node)
            throws SerializationException {
        String value = node.getString();
        if (value == null) {
            throw new SerializationException("Expected namespaced key");
        }

        NamespacedKey key = NamespacedKey.fromString(value);
        if (key == null) {
            throw new SerializationException("Invalid key: " + value);
        }
        return key;
    }

    @Override
    public void serialize(@NotNull Type type, @Nullable NamespacedKey obj, ConfigurationNode node
    ) throws SerializationException {
        node.set(obj == null ? null : obj.toString());
    }
}
