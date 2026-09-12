package net.astralounge.foundationapi.common.datamanagement.files.serialization.codec;

import com.google.gson.GsonBuilder;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.codec.interfaces.UnifiedCodec;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.configurate.PathSerializer;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.gson.PathGsonAdapter;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.nio.file.Path;

public class PathCodec implements UnifiedCodec<Path> {
    @Override
    public Class<Path> type() {
        return Path.class;
    }

    @Override
    public void registerConfigurate(TypeSerializerCollection.Builder builder) {
        builder.register(type(), new PathSerializer());
    }

    @Override
    public void registerGson(GsonBuilder builder) {
        builder.registerTypeAdapter(type(), new PathGsonAdapter());
    }
}
