package net.astralounge.foundationapi.paper.menumanager.v1.listeners;

import net.astralounge.foundationapi.paper.menumanager.v1.Menu;
import net.astralounge.foundationapi.paper.menumanager.v1.PaginatedMenu;
import net.astralounge.foundationapi.paper.menumanager.v1.menuitems.MenuItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class MenuListener implements Listener {

    Logger logger = LogManager.getLogger();

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {

        InventoryHolder holder = e.getInventory().getHolder();

        if (!(holder instanceof Menu menu)) {
            return;
        }

        ItemStack currentItem = e.getCurrentItem();

        if (currentItem == null) {
            return;
        }

        int slotClicked = e.getSlot();

        logger.debug("Clicked in Menu slot {}", slotClicked);

        // First check regular menu items
        MenuItem menuItem = menu.getMenuItem(slotClicked);

        // Then check paginated items
        if (menuItem == null && menu instanceof PaginatedMenu paginatedMenu) {
            menuItem = paginatedMenu.getPaginatedItem(slotClicked);
        }

        /*
         * There is no MenuItem defined for this slot.
         *
         * Do NOT cancel the event.
         */
        if (menuItem == null) {
            return;
        }

        menuItem.onClick(e, menu);
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent e){

        InventoryHolder holder = e.getInventory().getHolder();

        if (holder instanceof Menu menu){
            menu.handleClose();
        }

    }

}
