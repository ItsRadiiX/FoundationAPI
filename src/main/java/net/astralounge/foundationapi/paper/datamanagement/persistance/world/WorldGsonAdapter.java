package net.astralounge.foundationapi.paper.datamanagement.persistance.world;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.lang.reflect.Type;

public final class WorldGsonAdapter
        implements JsonSerializer<World>, JsonDeserializer<World> {

    @Override
    public JsonElement serialize(
            World src,
            Type type,
            JsonSerializationContext ctx) {
        return new JsonPrimitive(src.getName());
    }

    @Override
    public World deserialize(
            JsonElement json,
            Type type,
            JsonDeserializationContext ctx) {
        String name = json.getAsString();
        World world = Bukkit.getWorld(name);

        if (world == null) {
            throw new JsonParseException("World not loaded: " + name);
        }
        return world;
    }
}
