package com.itsradiix.foundationapi.common.textmanager;


import com.itsradiix.foundationapi.common.exceptions.InvalidMessagesException;
import com.itsradiix.foundationapi.common.playerinfo.PlayerInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
public final class TextCreator {

    private volatile static MiniMessage miniMessage;

    private static final Map<String, String> tags = new HashMap<>();

    private TextCreator() {}

    /*
            L I S T S
     */

    public static @NotNull Component create(List<String> text) {
        return create(text, null);
    }

    public static @NotNull Component create(List<String> text, PlayerInfo player, TagResolver... tagResolver) {
        String s = Placeholders.parsePlaceholder(player, combineIterable(text));
        return getMiniMessage().deserialize(s, tagResolver);
    }

    /*
            A R R A Y S
     */

    public static @NotNull Component create(String[] text) {
        return create(text, null);
    }

    public static @NotNull Component create(String[] text, PlayerInfo player, TagResolver... tagResolver) {
        String s = Placeholders.parsePlaceholder(player, combineIterable(List.of(text)));
        return getMiniMessage().deserialize(s, tagResolver);
    }

    /*
            S T R I N G S
     */

    public static @NotNull Component create(String text) {
        return create(text, null);
    }

    public static @NotNull Component create(String text, PlayerInfo player, TagResolver... tagResolver) {
        String s = Placeholders.parsePlaceholder(player, text);
        return getMiniMessage().deserialize(s, tagResolver);
    }


    /*
            M I N I     M E S S A G E S
     */

    public static MiniMessage getMiniMessage() {
        if (miniMessage == null) setupMiniMessage();
        return miniMessage;
    }

    private static void setupMiniMessage() {
        miniMessage = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolvers(createResolvers())
                        .build()
                )
                .build();
    }

    private static @NotNull Iterable<? extends TagResolver> createResolvers() {
        List<TagResolver> tagResolvers = new ArrayList<>();
        tagResolvers.add(StandardTags.defaults());

        for (Map.Entry<String, String> entry : tags.entrySet()) {
            @Subst("") String key = entry.getKey();
            String value = entry.getValue();
            tagResolvers.add(Placeholder.parsed(key, value));
        }

        return tagResolvers;
    }

    /*
            H E L P E R     M E T H O D S
     */

    @Contract("_ -> new")
    public static @NotNull String combineIterable(Iterable<? extends CharSequence> text) {
        return String.join("<newline>", text);
    }

    public static @NotNull TextComponent translateColorCodes(char character, String input) {
        return LegacyComponentSerializer.legacy(character).deserialize(Objects.requireNonNullElse(input, "&cA null value has been passed"));
    }

    @Contract("_, _ -> new")
    public static @NotNull TagResolver createPlaceholder(@Subst("") @NotNull String placeholder, String value) {
        return Placeholder.parsed(placeholder, value);
    }

    @Contract("_ -> new")
    public static @NotNull TagResolver getTargetTagResolver(String target) {
        return Placeholder.parsed("target", target);
    }

    public static void addTagResolver(String key, String value) {
        tags.put(key, value);

        if (miniMessage != null) {
            setupMiniMessage();
        }
    }

    public static void addTagResolvers(Map<String, String> newTags) {
        tags.putAll(newTags);

        if (miniMessage != null) {
            setupMiniMessage();
        }
    }

    public static String parseObjectToString(Object o) throws InvalidMessagesException {
        switch (o) {
            case String s -> {return s;}
            case String[] s -> {return TextCreator.combineIterable(Arrays.asList(s));}
            case List<?> s -> {return TextCreator.combineIterable(castToStringList(s));}
            case null, default -> throw new InvalidMessagesException("Your messages configuration is invalid! It contains an object that cannot be interpreted as a Message");
        }
    }

    public static List<String> castToStringList(List<?> list){
        List<String> stringList = new ArrayList<>();
        for (Object o : list){
            if (o instanceof String s){
                stringList.add(s);
            }
        }
        return  stringList;
    }
}
