package net.astralounge.foundationapi.common.datamanagement.files.serialization.configurate;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class LocalDateTimeSerializer
        implements TypeSerializer<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public LocalDateTime deserialize(
            @NonNull Type type, ConfigurationNode node)
            throws SerializationException {

        String value = node.getString();
        if (value == null) {
            throw new SerializationException("Expected a string for LocalDateTime");
        }
        return LocalDateTime.parse(value, FORMATTER);
    }

    @Override
    public void serialize(
            @NonNull Type type, @Nullable LocalDateTime obj,
            @NonNull ConfigurationNode node)
            throws SerializationException {
        if (obj == null) {
            node.set(null);
        } else {
            node.set(obj.format(FORMATTER));
        }
    }
}
