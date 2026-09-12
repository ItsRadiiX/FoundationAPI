package net.astralounge.foundationapi.common.datamanagement.files.serialization.configurate;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Locale;

public final class LocaleSerializer implements TypeSerializer<Locale> {

    @Override
    public Locale deserialize(@NonNull Type type, ConfigurationNode node) throws SerializationException {
        String value = node.getString();
        if (value == null) {
            throw new SerializationException("Expected locale string");
        }
        return Locale.forLanguageTag(value.replace('_', '-'));
    }

    @Override
    public void serialize(@NonNull Type type, @Nullable Locale obj, ConfigurationNode node
    ) throws SerializationException {
        node.set(obj == null ? null : obj.toLanguageTag());
    }
}
