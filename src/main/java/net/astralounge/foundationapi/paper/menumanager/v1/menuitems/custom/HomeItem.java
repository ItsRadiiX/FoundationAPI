package net.astralounge.foundationapi.paper.menumanager.v1.menuitems.custom;

import net.astralounge.foundationapi.paper.itemmanager.ItemStackCreator;
import net.astralounge.foundationapi.paper.plugin.FoundationPaperPlugin;

public abstract class HomeItem extends SkullMenuItem {

    private static String HOME_SKULL =
            "http://textures.minecraft.net/texture/12d7a751eb071e08dbbc95bc5d9d66e5f51dc6712640ad2dfa03defbb68a7f3a";

    private final String homeName;

    public HomeItem(FoundationPaperPlugin<?,?> plugin, String homeName) {
        super(plugin, HOME_SKULL);
        this.homeName = homeName;
    }

    @Override
    protected ItemStackCreator makeSkull(ItemStackCreator creator) {
        return creator.setName("<gold>Return to " + homeName);
    }
}
