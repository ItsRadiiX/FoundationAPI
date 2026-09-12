package net.astralounge.foundationapi.paper.datamanagement.persistance.location;

import com.google.gson.GsonBuilder;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.codec.interfaces.UnifiedCodec;
import org.bukkit.Location;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public class LocationCodec implements UnifiedCodec<Location> {
    @Override
    public Class<Location> type() {
        return Location.class;
    }

    @Override
    public void registerConfigurate(TypeSerializerCollection.Builder builder) {
        builder.register(Location.class, new LocationSerializer());
    }

    @Override
    public void registerGson(GsonBuilder builder) {
        builder.registerTypeAdapter(Location.class, new LocationGsonAdapter());
    }
}
