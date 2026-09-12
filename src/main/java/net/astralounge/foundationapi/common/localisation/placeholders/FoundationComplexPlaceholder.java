package net.astralounge.foundationapi.common.localisation.placeholders;

import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;

import java.util.UUID;
import java.util.function.BiFunction;

public interface FoundationComplexPlaceholder extends FoundationPlaceholder, BiFunction<ArgumentQueue, Context, Tag> {
    String resolveData(UUID playerOne);
}
