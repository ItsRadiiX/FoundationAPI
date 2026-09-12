package net.astralounge.foundationapi.common.datamanagement.files.serialization.codec;

import com.google.gson.GsonBuilder;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.codec.interfaces.UnifiedCodec;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.configurate.LocaleSerializer;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.gson.LocaleGsonAdapter;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.Locale;

public final class LocaleCodec implements UnifiedCodec<Locale> {

    @Override
    public Class<Locale> type() {
        return Locale.class;
    }

    @Override
    public void registerConfigurate(TypeSerializerCollection.Builder builder) {
        builder.register(Locale.class, new LocaleSerializer());
    }

    @Override
    public void registerGson(GsonBuilder builder) {
        builder.registerTypeAdapter(Locale.class, new LocaleGsonAdapter());
    }
}
