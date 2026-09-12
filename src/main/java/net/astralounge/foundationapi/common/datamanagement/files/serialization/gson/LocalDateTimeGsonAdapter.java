package net.astralounge.foundationapi.common.datamanagement.files.serialization.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

public final class LocalDateTimeGsonAdapter
        implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    @Override
    public JsonElement serialize(
            LocalDateTime src,
            Type type,
            JsonSerializationContext ctx
    ) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public LocalDateTime deserialize(
            JsonElement json,
            Type type,
            JsonDeserializationContext ctx
    ) throws JsonParseException {
        return LocalDateTime.parse(json.getAsString());
    }
}
