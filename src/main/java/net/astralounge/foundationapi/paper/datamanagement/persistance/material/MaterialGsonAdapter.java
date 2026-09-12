package net.astralounge.foundationapi.paper.datamanagement.persistance.material;

import com.google.gson.*;
import org.bukkit.Material;

import java.lang.reflect.Type;

public final class MaterialGsonAdapter implements JsonSerializer<Material>, JsonDeserializer<Material> {

    @Override
    public JsonElement serialize(
            Material src,
            Type type,
            JsonSerializationContext ctx) {
        return new JsonPrimitive(src.getKey().toString());
    }

    @Override
    public Material deserialize(
            JsonElement json,
            Type type,
            JsonDeserializationContext ctx) {
        Material material = Material.matchMaterial(json.getAsString());

        if (material == null) {
            throw new JsonParseException(
                    "Unknown material: " + json.getAsString()
            );
        }
        return material;
    }
}
