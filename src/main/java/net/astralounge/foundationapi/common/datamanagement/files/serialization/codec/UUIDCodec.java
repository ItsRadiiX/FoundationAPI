package net.astralounge.foundationapi.common.datamanagement.files.serialization.codec;

import com.google.gson.GsonBuilder;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.codec.interfaces.UnifiedCodec;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.configurate.UUIDTypeSerializer;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.gson.UUIDGsonAdapter;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.UUID;

public final class UUIDCodec implements UnifiedCodec<UUID> {

    @Override
    public Class<UUID> type() {
        return UUID.class;
    }

    @Override
    public void registerConfigurate(TypeSerializerCollection.Builder builder) {
        builder.register(UUID.class, new UUIDTypeSerializer());
    }

    @Override
    public void registerGson(GsonBuilder builder) {
        builder.registerTypeAdapter(UUID.class, new UUIDGsonAdapter());
    }
}
