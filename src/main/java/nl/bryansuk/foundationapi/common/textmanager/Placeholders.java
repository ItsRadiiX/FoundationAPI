package nl.bryansuk.foundationapi.common.textmanager;

import nl.bryansuk.foundationapi.common.playerinfo.PlayerInfo;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public final class Placeholders {

    private static Placeholders instance;
    private final List<BiFunction<@Nullable PlayerInfo, String, String>> parsers = new ArrayList<>();

    public Placeholders(){
        if (instance == null) instance = this;
    }

    public static String parsePlaceholder(@Nullable PlayerInfo offlinePlayer, String payload){
        for (BiFunction<PlayerInfo, String, String> parser : getInstance().parsers) {
            try {
                String parsedPayload = parser.apply(offlinePlayer, payload);
                if (parsedPayload != null) return parsedPayload;
            } catch (Exception ignored) {}
        }
        return (payload);
    }

    public static void addParser(BiFunction<@Nullable PlayerInfo, String, String> parser){
        getInstance().parsers.add(parser);
    }

    public static Placeholders getInstance() {
        return instance;
    }
}
