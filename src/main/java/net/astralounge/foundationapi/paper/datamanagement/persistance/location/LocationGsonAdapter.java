package net.astralounge.foundationapi.paper.datamanagement.persistance.location;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;

public final class LocationGsonAdapter
        implements JsonSerializer<Location>, JsonDeserializer<Location> {

    public LocationGsonAdapter() {}

    @Override
    public JsonElement serialize(
            Location src,
            Type type,
            JsonSerializationContext ctx
    ) {

        JsonObject obj = new JsonObject();
        obj.addProperty("world", src.getWorld().getName());
        obj.addProperty("x", src.getX());
        obj.addProperty("y", src.getY());
        obj.addProperty("z", src.getZ());
        obj.addProperty("yaw", src.getYaw());
        obj.addProperty("pitch", src.getPitch());

        return obj;
    }

    @Override
    public Location deserialize(
            JsonElement json,
            Type type,
            JsonDeserializationContext ctx
    ) throws JsonParseException {

        JsonObject obj = json.getAsJsonObject();

        String worldName = obj.get("world").getAsString();
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new JsonParseException(
                    "World not loaded: " + worldName
            );
        }

        return new Location(
                world,
                obj.get("x").getAsDouble(),
                obj.get("y").getAsDouble(),
                obj.get("z").getAsDouble(),
                obj.get("yaw").getAsFloat(),
                obj.get("pitch").getAsFloat()
        );
    }
}
