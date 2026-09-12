package net.astralounge.foundationapi.common.datamanagement.files.serialization.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Locale;

public final class LocaleGsonAdapter
        implements JsonSerializer<Locale>, JsonDeserializer<Locale> {

    @Override
    public JsonElement serialize(
            Locale src,
            Type type,
            JsonSerializationContext ctx
    ) {
        return new JsonPrimitive(src.toLanguageTag());
    }

    @Override
    public Locale deserialize(
            JsonElement json,
            Type type,
            JsonDeserializationContext ctx
    ) {
        return Locale.forLanguageTag(json.getAsString());
    }
}
