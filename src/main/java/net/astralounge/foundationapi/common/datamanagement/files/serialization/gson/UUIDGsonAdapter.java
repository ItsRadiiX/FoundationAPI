package net.astralounge.foundationapi.common.datamanagement.files.serialization.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.UUID;

public final class UUIDGsonAdapter
        implements JsonSerializer<UUID>, JsonDeserializer<UUID> {

    @Override
    public JsonElement serialize(UUID src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return null;
        return new JsonPrimitive(src.toString());
    }

    @Override
    public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        if (json == null || json.isJsonNull()) {
            return null;
        }

        if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) {
            throw new JsonParseException(
                    "Expected UUID as string, got: " + json);
        }

        try {
            return UUID.fromString(json.getAsString());
        } catch (IllegalArgumentException ex) {
            throw new JsonParseException(
                    "Invalid UUID string: " + json.getAsString(), ex);
        }
    }
}
