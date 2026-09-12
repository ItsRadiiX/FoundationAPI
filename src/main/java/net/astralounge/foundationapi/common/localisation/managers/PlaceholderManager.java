package net.astralounge.foundationapi.common.localisation.managers;

import net.astralounge.foundationapi.common.localisation.placeholders.FoundationComplexPlaceholder;
import net.astralounge.foundationapi.common.localisation.placeholders.FoundationComplexRelationalPlaceholder;
import net.astralounge.foundationapi.common.localisation.placeholders.FoundationPlaceholder;
import net.astralounge.foundationapi.common.localisation.placeholders.PlaceholderInformation;
import net.astralounge.foundationapi.common.logger.service.FoundationLoggerService;
import net.astralounge.foundationapi.common.manager.CommonManager;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class PlaceholderManager extends CommonManager {

    private final Map<String, FoundationPlaceholder> placeholders;
    private final List<Function<PlaceholderInformation, String>> parsers;

    public PlaceholderManager(FoundationDefaultPlugin<?> plugin) {
        super(plugin);
        this.placeholders = new HashMap<>();
        this.parsers = new ArrayList<>();
    }

    @Override
    public void onLoad() throws Exception {

    }

    @Override
    public void onEnable() throws Exception {

    }

    @Override
    public void onDisable() throws Exception {

    }

    @Override
    public Collection<Class<? extends CommonManager>> getCommonDependencies() {
        return Collections.emptyList();
    }

    public void addPlaceholders(List<FoundationPlaceholder> placeholders) {
        for (FoundationPlaceholder placeholder : placeholders) addPlaceholder(placeholder);
    }

    public void addPlaceholder(FoundationPlaceholder placeholder) {
        if (placeholders.containsKey(placeholder.getTagIdentifier())) {
            FoundationLoggerService.getGlobalLogger()
                    .warn("Placeholder with tag identifier " + placeholder.getTagIdentifier() + " already exists!");
            return;
        }
        placeholders.put(placeholder.getTagIdentifier(), placeholder);
    }

    public String parsePlaceholders(PlaceholderInformation placeholderInformation) {
        for (Function<PlaceholderInformation, String> parser : parsers) {
            try {
                String parsedPayload = parser.apply(placeholderInformation);
                if (parsedPayload != null) return parsedPayload;
            } catch (Exception ignored) {
            }
        }
        return (placeholderInformation.payload());
    }

    public void addParser(Function<PlaceholderInformation, String> parser) {
        parsers.add(parser);
    }

    public @NotNull Iterable<? extends TagResolver> createResolvers() {
        List<TagResolver> tagResolvers = new ArrayList<>();
        tagResolvers.add(StandardTags.defaults());

        for (Map.Entry<String, FoundationPlaceholder> entry : placeholders.entrySet()) {
            @Subst("") String key = entry.getKey();
            FoundationPlaceholder placeholder = entry.getValue();

            if (placeholder instanceof FoundationComplexPlaceholder complexPlaceholder) {
                tagResolvers.add(TagResolver.resolver(key, complexPlaceholder));
            } else {
                tagResolvers.add(Placeholder.parsed(key, placeholder.resolveStatic()));
            }
        }

        return tagResolvers;
    }

    public @Nullable FoundationPlaceholder getPlaceholder(String identifier) {
        return placeholders.get(identifier);
    }

    public @Nullable FoundationComplexPlaceholder getComplexPlaceholder(String identifier) {
        FoundationPlaceholder placeholder = getPlaceholder(identifier);
        if (placeholder instanceof FoundationComplexPlaceholder complexPlaceholder) return complexPlaceholder;
        return null;
    }

    public @Nullable FoundationComplexRelationalPlaceholder getComplexRelationalPlaceholder(String identifier) {
        FoundationPlaceholder placeholder = getPlaceholder(identifier);
        if (placeholder instanceof FoundationComplexRelationalPlaceholder complexPlaceholder) return complexPlaceholder;
        return null;
    }
}
