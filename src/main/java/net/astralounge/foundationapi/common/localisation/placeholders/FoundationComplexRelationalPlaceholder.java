package net.astralounge.foundationapi.common.localisation.placeholders;

import java.util.UUID;

public interface FoundationComplexRelationalPlaceholder extends FoundationComplexPlaceholder {
    String resolveData(UUID playerOne, UUID playerTwo);
}
