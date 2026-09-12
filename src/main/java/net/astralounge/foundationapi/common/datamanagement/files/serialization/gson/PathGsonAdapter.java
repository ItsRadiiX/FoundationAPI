package net.astralounge.foundationapi.common.datamanagement.files.serialization.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class PathGsonAdapter
        implements JsonSerializer<Path>, JsonDeserializer<Path> {

    @Override
    public JsonElement serialize(
            Path src,
            Type type,
            JsonSerializationContext ctx) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public Path deserialize(
            JsonElement json,
            Type type,
            JsonDeserializationContext ctx) {
        return Paths.get(json.getAsString());
    }
}
