package net.astralounge.foundationapi.common.localisation;

import net.astralounge.foundationapi.common.localisation.managers.PlaceholderManager;
import net.astralounge.foundationapi.common.localisation.placeholders.PlaceholderInformation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Plugin-agnostic text creator that uses the global PlaceholderManager and
 * global FoundationMiniMessage from LocalisationService.
 */
public final class TextCreator {

    public TextCreator() {
    }

    /*
            L I S T S
     */

    public @NotNull Component create(List<String> text) {
        return create(text, null);
    }

    public @NotNull Component create(List<String> text, UUID player, TagResolver... tagResolvers) {
        return create(text, player, null, tagResolvers);
    }

    public @NotNull Component create(List<String> text, UUID playerOne, UUID playerTwo, TagResolver... tagResolvers) {
        return createInternal(new PlaceholderInformation(playerOne, playerTwo, combineIterable(text)), tagResolvers);
    }

    /*
            A R R A Y S
     */

    public @NotNull Component create(String[] text) {
        return create(text, null);
    }

    public @NotNull Component create(String[] text, UUID player, TagResolver... tagResolvers) {
        return create(text, player, null, tagResolvers);
    }

    public @NotNull Component create(String[] text, UUID playerOne, UUID playerTwo, TagResolver... tagResolvers) {
        return createInternal(new PlaceholderInformation(playerOne, playerTwo, combineIterable(List.of(text))), tagResolvers);
    }

    /*
            C O M P O N E N T S
     */

    public @NotNull Component create(Component text) {
        return create(text, null);
    }

    public @NotNull Component create(Component text, UUID player, TagResolver... tagResolvers) {
        return create(text, player, null, tagResolvers);
    }

    public @NotNull Component create(Component component, UUID playerOne, UUID playerTwo, TagResolver... tagResolvers) {
        if (component instanceof TextComponent textComponent) {
            return createInternal(new PlaceholderInformation(playerOne, playerTwo, textComponent.content()), tagResolvers);
        }
        return component;
    }

    /*
            S T R I N G S
     */

    public @NotNull Component create(String text) {
        return create(text, null);
    }

    public @NotNull Component create(String text, UUID player, TagResolver... tagResolvers) {
        return create(text, player, null, tagResolvers);
    }

    public @NotNull Component create(String text, UUID playerOne, UUID playerTwo, TagResolver... tagResolvers) {
        return createInternal(new PlaceholderInformation(playerOne, playerTwo, text), tagResolvers);
    }

    /*
            H E L P E R     M E T H O D S
     */

    private Component createInternal(
            PlaceholderInformation placeholderInformation,
            TagResolver... tagResolvers
    ) {
        PlaceholderManager placeholderManager = LocalisationService.getInstance().getPlaceholderManager();
        FoundationMiniMessage foundationMiniMessage = LocalisationService.getInstance().getFoundationMiniMessage();

        String parsedPlaceholders = parsePlaceholders(placeholderManager, placeholderInformation);
        return parseMiniMessages(foundationMiniMessage, parsedPlaceholders, tagResolvers);
    }

    public Component createUsingCachedLegacy(
            PlaceholderInformation placeholderInformation,
            TagResolver... tagResolvers
    ) {
        PlaceholderManager placeholderManager = LocalisationService.getInstance().getPlaceholderManager();
        FoundationMiniMessage foundationMiniMessage = LocalisationService.getInstance().getFoundationMiniMessage();

        String parsedPlaceholders = parsePlaceholders(placeholderManager, placeholderInformation);
        return parseMiniMessagesCachedLegacy(foundationMiniMessage, parsedPlaceholders, tagResolvers);
    }

    private String parsePlaceholders(
            PlaceholderManager placeholderManager,
            PlaceholderInformation placeholderInformation) {
        return placeholderManager.parsePlaceholders(placeholderInformation);
    }

    private Component parseMiniMessages(
            FoundationMiniMessage foundationMiniMessage,
            String input,
            TagResolver... tagResolvers
    ) {
        MiniMessage miniMessage = foundationMiniMessage.getMiniMessage();
        return miniMessage.deserialize(LegacyMiniMessageConverter.legacyToMiniFast(input), tagResolvers);
    }

    private Component parseMiniMessagesCachedLegacy(
            FoundationMiniMessage foundationMiniMessage,
            String input,
            TagResolver... tagResolvers
    ) {
        MiniMessage miniMessage = foundationMiniMessage.getMiniMessage();
        return miniMessage.deserialize(input, tagResolvers);
    }

    @Contract("_ -> new")
    public static @NotNull String combineIterable(Iterable<? extends CharSequence> text) {
        return String.join("<newline>", text);
    }
}