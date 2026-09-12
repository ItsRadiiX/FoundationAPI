package net.astralounge.foundationapi.common.localisation.placeholders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record PlaceholderInformation(@Nullable UUID playerOne, @Nullable UUID playerTwo, @NotNull String payload) {
}
