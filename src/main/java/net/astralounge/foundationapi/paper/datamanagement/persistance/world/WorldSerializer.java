package net.astralounge.foundationapi.paper.datamanagement.persistance.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class WorldSerializer implements TypeSerializer<World> {

    @Override
    public World deserialize(@NonNull Type type, ConfigurationNode node)
            throws SerializationException {
        String name = node.getString();
        if (name == null) {
            throw new SerializationException("World name required");
        }

        World world = Bukkit.getWorld(name);
        if (world == null) {
            throw new SerializationException("World not loaded: " + name);
        }

        return world;
    }

    @Override
    public void serialize(@NonNull Type type, @Nullable World obj, ConfigurationNode node
    ) throws SerializationException {
        node.set(obj == null ? null : obj.getName());
    }
}
