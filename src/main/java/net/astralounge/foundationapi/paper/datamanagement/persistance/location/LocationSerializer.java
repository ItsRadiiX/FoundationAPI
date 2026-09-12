package net.astralounge.foundationapi.paper.datamanagement.persistance.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class LocationSerializer
        implements TypeSerializer<Location> {

    @Override
    public Location deserialize(@NonNull Type type, ConfigurationNode node)
            throws SerializationException {

        String worldName = node.node("world").getString();
        if (worldName == null) {
            throw new SerializationException("Missing world");
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new SerializationException("World not loaded: " + worldName);
        }

        return new Location(
                world,
                node.node("x").getDouble(),
                node.node("y").getDouble(),
                node.node("z").getDouble(),
                (float) node.node("yaw").getDouble(),
                (float) node.node("pitch").getDouble()
        );
    }

    @Override
    public void serialize(
            @NonNull Type type,
            @Nullable Location loc,
            @NonNull ConfigurationNode node) throws SerializationException {

        if (loc == null) {
            node.set(null);
            return;
        }

        node.node("world").set(loc.getWorld().getName());
        node.node("x").set(loc.getX());
        node.node("y").set(loc.getY());
        node.node("z").set(loc.getZ());
        node.node("yaw").set(loc.getYaw());
        node.node("pitch").set(loc.getPitch());
    }
}
