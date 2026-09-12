package net.astralounge.foundationapi.common.datamanagement.files.serialization.codec;

import com.google.gson.GsonBuilder;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.codec.interfaces.UnifiedCodec;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.configurate.ComponentSerializer;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.gson.ComponentGsonAdapter;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public class ComponentCodec implements UnifiedCodec<Component> {

    private final FoundationDefaultPlugin<?> plugin;

    public ComponentCodec(FoundationDefaultPlugin<?> plugin) {
        this.plugin = plugin;
    }

    @Override
    public Class<Component> type() {
        return Component.class;
    }

    @Override
    public void registerConfigurate(TypeSerializerCollection.Builder builder) {
        builder.register(type(), new ComponentSerializer(plugin));
    }

    @Override
    public void registerGson(GsonBuilder builder) {
        builder.registerTypeAdapter(type(), new ComponentGsonAdapter(plugin));
    }
}
