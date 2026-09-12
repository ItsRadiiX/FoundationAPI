package net.astralounge.foundationapi.common.datamanagement.files.serialization.codec;

import com.google.gson.GsonBuilder;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.codec.interfaces.UnifiedCodec;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.configurate.LocalDateTimeSerializer;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.gson.LocalDateTimeGsonAdapter;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.time.LocalDateTime;

public final class LocalDateTimeCodec implements UnifiedCodec<LocalDateTime> {

    @Override
    public Class<LocalDateTime> type() {
        return LocalDateTime.class;
    }

    @Override
    public void registerConfigurate(TypeSerializerCollection.Builder builder) {
        builder.register(LocalDateTime.class, new LocalDateTimeSerializer());
    }

    @Override
    public void registerGson(GsonBuilder builder) {
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeGsonAdapter());
    }
}
