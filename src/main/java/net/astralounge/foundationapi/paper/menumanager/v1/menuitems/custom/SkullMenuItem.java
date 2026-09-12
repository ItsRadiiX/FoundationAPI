package net.astralounge.foundationapi.paper.menumanager.v1.menuitems.custom;

import net.astralounge.foundationapi.paper.itemmanager.ItemStackCreator;
import net.astralounge.foundationapi.paper.menumanager.v1.menuitems.MenuItem;
import net.astralounge.foundationapi.paper.plugin.FoundationPaperPlugin;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class SkullMenuItem extends MenuItem {

    private URL url;

    public SkullMenuItem(FoundationPaperPlugin<?,?> plugin, URL url) {
        super(plugin);
        this.url = url;
    }

    public SkullMenuItem(FoundationPaperPlugin<?,?> plugin, String url) {
        super(plugin);
        try {
            this.url = new URI(url).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            plugin.getFoundationPluginLogger().error("Failed getting URL from " + url, e);
        }
    }


    protected abstract ItemStackCreator makeSkull(ItemStackCreator creator);

    @Override
    public @NotNull ItemStackCreator defineItemStack() {
        ItemStackCreator creator = getItemFactory().builder(Material.PLAYER_HEAD);
        try {
            if (url != null) {
                creator.setSkullFromUrl(url);
            }
        } catch (Exception e) {
            getMenuManager().getPlugin().getFoundationPluginLogger().error("Failed to load skull from url!", e);
        }

        return makeSkull(creator);
    }
}
