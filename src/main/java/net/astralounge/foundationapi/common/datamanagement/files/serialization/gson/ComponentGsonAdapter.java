package net.astralounge.foundationapi.common.datamanagement.files.serialization.gson;

import com.google.gson.*;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import net.kyori.adventure.text.Component;

import java.lang.reflect.Type;

public final class ComponentGsonAdapter
        implements JsonSerializer<Component>, JsonDeserializer<Component> {

    private final FoundationDefaultPlugin<?> plugin;

    public ComponentGsonAdapter(FoundationDefaultPlugin<?> plugin) {
        this.plugin = plugin;
    }

    @Override
    public JsonElement serialize(
            Component src,
            Type type,
            JsonSerializationContext ctx) {
        return new JsonPrimitive(
                plugin.getMessagesManager().getFoundationMiniMessage().getMiniMessage().serialize(src)
        );
    }

    @Override
    public Component deserialize(
            JsonElement json,
            Type type,
            JsonDeserializationContext ctx) {
        return plugin.getMessagesManager().getTextCreator().create(json.getAsString());
    }
}
