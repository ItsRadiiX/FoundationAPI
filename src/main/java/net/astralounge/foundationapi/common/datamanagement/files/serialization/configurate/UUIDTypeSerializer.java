package net.astralounge.foundationapi.common.datamanagement.files.serialization.configurate;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.UUID;

public final class UUIDTypeSerializer implements TypeSerializer<UUID> {

    @Override
    public UUID deserialize(@NonNull Type type, ConfigurationNode node) throws SerializationException {
        String value = node.getString();
        if (value == null) {
            throw new SerializationException("Expected UUID string");
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            throw new SerializationException(ex);
        }
    }

    @Override
    public void serialize(@NonNull Type type, @Nullable UUID obj, ConfigurationNode node
    ) throws SerializationException {
        node.set(obj == null ? null : obj.toString());
    }
}
