package net.astralounge.foundationapi.common.datamanagement.files.serialization;

import com.google.gson.GsonBuilder;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.codec.interfaces.UnifiedCodec;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.ArrayList;
import java.util.List;

public final class SerializerBundle {

    private final List<UnifiedCodec<?>> codecs;

    private SerializerBundle(List<UnifiedCodec<?>> codecs) {
        this.codecs = List.copyOf(codecs);
    }

    public static SerializerBundle of(UnifiedCodec<?>... codecs) {
        return new SerializerBundle(List.of(codecs));
    }

    public static SerializerBundle merge(SerializerBundle... bundles) {
        List<UnifiedCodec<?>> merged = new ArrayList<>();

        for (SerializerBundle bundle : bundles) {
            merged.addAll(bundle.codecs);
        }

        return new SerializerBundle(merged);
    }

    public void applyConfigurate(
            TypeSerializerCollection.Builder builder
    ) {
        codecs.forEach(c -> c.registerConfigurate(builder));
    }

    public void applyGson(GsonBuilder builder) {
        codecs.forEach(c -> c.registerGson(builder));
    }
}
