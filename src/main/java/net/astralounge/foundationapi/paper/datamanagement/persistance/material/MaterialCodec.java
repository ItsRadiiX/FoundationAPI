package net.astralounge.foundationapi.paper.datamanagement.persistance.material;

import com.google.gson.GsonBuilder;
import net.astralounge.foundationapi.common.datamanagement.files.serialization.codec.interfaces.UnifiedCodec;
import org.bukkit.Material;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public class MaterialCodec implements UnifiedCodec<Material> {
    @Override
    public Class<Material> type() {
        return Material.class;
    }

    @Override
    public void registerConfigurate(TypeSerializerCollection.Builder builder) {
        builder.register(type(), new MaterialSerializer());
    }

    @Override
    public void registerGson(GsonBuilder builder) {
        builder.registerTypeAdapter(type(), new MaterialGsonAdapter());
    }
}
