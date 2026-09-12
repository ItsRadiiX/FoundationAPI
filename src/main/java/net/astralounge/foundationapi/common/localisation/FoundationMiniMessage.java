package net.astralounge.foundationapi.common.localisation;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

/**
 * Global MiniMessage wrapper that always uses the global PlaceholderManager.
 */
public class FoundationMiniMessage {

    private volatile MiniMessage miniMessage;

    public void reloadMiniMessage() {
        setupMiniMessage();
    }

    public MiniMessage getMiniMessage() {
        if (miniMessage == null) setupMiniMessage();
        return miniMessage;
    }

    private void setupMiniMessage() {
        miniMessage = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolvers(
                                LocalisationService
                                        .getInstance()
                                        .getPlaceholderManager()
                                        .createResolvers()
                        )
                        .build()
                )
                .build();
    }
}