package net.astralounge.foundationapi.paper.datamanagement.persistance.world;

import com.google.gson.GsonBuilder;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.codec.interfaces.UnifiedCodec;
import org.bukkit.World;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public class WorldCodec implements UnifiedCodec<World> {
    @Override
    public Class<World> type() {
        return World.class;
    }

    @Override
    public void registerConfigurate(TypeSerializerCollection.Builder builder) {
        builder.register(type(), new WorldSerializer());
    }

    @Override
    public void registerGson(GsonBuilder builder) {
        builder.registerTypeAdapter(type(), new WorldGsonAdapter());
    }
}
