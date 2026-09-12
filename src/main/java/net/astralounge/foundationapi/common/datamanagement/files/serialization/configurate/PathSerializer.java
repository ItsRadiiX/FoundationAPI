package net.astralounge.foundationapi.common.datamanagement.files.serialization.configurate;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class PathSerializer implements TypeSerializer<Path> {

    @Override
    public Path deserialize(@NonNull Type type, ConfigurationNode node)
            throws SerializationException {
        String value = node.getString();
        if (value == null) {
            throw new SerializationException("Expected path string");
        }
        return Paths.get(value);
    }

    @Override
    public void serialize(@NonNull Type type, @Nullable Path obj, ConfigurationNode node
    ) throws SerializationException {
        node.set(obj == null ? null : obj.toString());
    }
}