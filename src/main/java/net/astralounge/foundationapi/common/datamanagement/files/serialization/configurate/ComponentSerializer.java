package net.astralounge.foundationapi.common.datamanagement.files.serialization.configurate;

import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class ComponentSerializer
        implements TypeSerializer<Component> {

    private final FoundationDefaultPlugin<?> foundationDefaultPlugin;

    public ComponentSerializer(FoundationDefaultPlugin<?> foundationDefaultPlugin) {
        this.foundationDefaultPlugin = foundationDefaultPlugin;
    }

    @Override
    public Component deserialize(@NonNull Type type, ConfigurationNode node) throws SerializationException {

        String value = node.getString();
        if (value == null) {
            return Component.empty();
        }

        try {
            return getMiniMessage().deserialize(value);
        } catch (Exception ex) {
            throw new SerializationException("Invalid MiniMessage component: " + value);
        }
    }

    @Override
    public void serialize(
            Type type,
            @Nullable Component obj,
            ConfigurationNode node
    ) throws SerializationException {

        if (obj == null) {
            node.set(null);
            return;
        }

        node.set(getMiniMessage().serialize(obj));
    }

    private MiniMessage getMiniMessage() {
        return foundationDefaultPlugin.getMessagesManager().getMiniMessage();
    }
}
