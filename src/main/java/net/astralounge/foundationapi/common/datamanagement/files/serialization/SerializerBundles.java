package net.astralounge.foundationapi.common.datamanagement.files.serialization;

import net.astralounge.foundationapi.common.datamanagement.files.serialization.codec.*;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import net.astralounge.foundationapi.paper.datamanagement.persistance.location.LocationCodec;
import net.astralounge.foundationapi.paper.datamanagement.persistance.material.MaterialCodec;
import net.astralounge.foundationapi.paper.datamanagement.persistance.world.WorldCodec;

public final class SerializerBundles {

    private SerializerBundles() {
    }

    public static SerializerBundle javaUtil() {
        return SerializerBundle.of(new UUIDCodec(), new LocalDateTimeCodec(), new LocaleCodec());
    }


    /**
     * Bukkit support
     */
    public static SerializerBundle bukkit() {
        return SerializerBundle.of(new LocationCodec(), new MaterialCodec(), new WorldCodec());

    }

    /**
     * Adventure API support
     */
    public static SerializerBundle adventureAPI(FoundationDefaultPlugin<?> plugin) {
        return SerializerBundle.of(new ComponentCodec(plugin));
    }

    /**
     * Everything supported by FoundationAPI
     */
    public static SerializerBundle all(FoundationDefaultPlugin<?> plugin) {
        return SerializerBundle.merge(
                javaUtil(),
                bukkit(),
                adventureAPI(plugin)
        );
    }


}
