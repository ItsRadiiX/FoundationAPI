package net.astralounge.foundationapi.common.localisation.placeholders;

public interface FoundationPlaceholder {
    String getTagIdentifier();

    /**
     * Static value for this placeholder. Called once when building
     * the MiniMessage TagResolver set.
     */
    String resolveStatic();
}
