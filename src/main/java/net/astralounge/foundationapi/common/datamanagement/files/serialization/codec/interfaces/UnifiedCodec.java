package net.astralounge.foundationapi.common.datamanagement.files.serialization.codec.interfaces;

import com.google.gson.GsonBuilder;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public interface UnifiedCodec<T> {
    Class<T> type();

    void registerConfigurate(TypeSerializerCollection.Builder builder);
    void registerGson(GsonBuilder builder);
}
