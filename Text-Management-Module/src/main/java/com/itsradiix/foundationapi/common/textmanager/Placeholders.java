package com.itsradiix.foundationapi.common.textmanager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

public final class Placeholders {

    private static final List<BiFunction<@Nullable UUID, String, String>> parsers = new ArrayList<>();

    private Placeholders() {}

    public static String parsePlaceholder(@Nullable UUID uuid, String payload){
        for (BiFunction<UUID, String, String> parser : parsers) {
            try {
                String parsedPayload = parser.apply(uuid, payload);
                if (parsedPayload != null) return parsedPayload;
            } catch (Exception ignored) {}
        }
        return (payload);
    }

    public static void addParser(BiFunction<@Nullable UUID, String, String> parser){
        parsers.add(parser);
    }
}
