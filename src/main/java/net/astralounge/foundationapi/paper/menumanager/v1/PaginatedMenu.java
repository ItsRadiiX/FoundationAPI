package net.astralounge.foundationapi.paper.menumanager.v1;


import net.astralounge.foundationapi.paper.menumanager.v1.menuitems.MenuItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0;

    /**
     * The maximum number of items displayed on one page.
     *
     * A 6-row chest with a border has:
     *
     * 4 inner rows × 7 inner columns = 28 slots.
     */
    protected int maxItemsPerPage = 28;

    /**
     * Keeps track of which MenuItem is currently displayed
     * in each paginated inventory slot.
     */
    private final Map<Integer, MenuItem> paginatedItems = new HashMap<>();

    public PaginatedMenu(MenuManager<?> menuManager, Player player) {
        super(menuManager, player);
    }

    /**
     * @return the complete list of items being paginated.
     */
    public abstract List<MenuItem> getPageMenuItems();

    /**
     * Places the current page's items into the inventory.
     */
    public void setPaginatedMenuItems() {
        paginatedItems.clear();

        List<MenuItem> data = getPageMenuItems();

        if (data == null || data.isEmpty()) {
            return;
        }

        List<Integer> slots = getPaginatedSlots();

        int maxPage = (data.size() - 1) / getMaxItemsPerPage();

        if (page >= maxPage) {
            page = maxPage;
        }

        int dataStart = page * getMaxItemsPerPage();

        for (int i = 0; i < slots.size(); i++) {

            int dataIndex = dataStart + i;

            if (dataIndex >= data.size()) {
                break;
            }

            MenuItem menuItem = data.get(dataIndex);

            if (menuItem == null) {
                continue;
            }

            int inventorySlot = slots.get(i);

            paginatedItems.put(inventorySlot, menuItem);

            setItem(inventorySlot, menuItem);
        }
    }

    /**
     * Gets the MenuItem currently displayed at a paginated slot.
     */
    public MenuItem getPaginatedItem(int slot) {
        return paginatedItems.get(slot);
    }

    /**
     * Returns the inventory slots used for paginated items.
     *
     * For a 6-row inventory:
     *
     * 10 11 12 13 14 15 16
     * 19 20 21 22 23 24 25
     * 28 29 30 31 32 33 34
     * 37 38 39 40 41 42 43
     */
    protected List<Integer> getPaginatedSlots() {
        List<Integer> slots = new ArrayList<>();

        final int columns = 9;

        for (int row = 1; row < getRows() - 1; row++) {
            for (int column = 1; column < columns - 1; column++) {
                slots.add(row * columns + column);
            }
        }

        return slots;
    }

    /**
     * Clears the area used for paginated items.
     */
    public static void clearPaginatedItems(Inventory inventory, int rows) {
        final int columns = 9;

        for (int row = 1; row < rows - 1; row++) {
            for (int column = 1; column < columns - 1; column++) {
                inventory.setItem(row * columns + column, null);
            }
        }
    }

    /**
     * Moves to the previous page.
     */
    public boolean prevPage() {
        if (page <= 0) {
            return false;
        }

        page--;

        paginatedItems.clear();

        clearPaginatedItems(inventory, getRows());
        setPaginatedMenuItems();

        return true;
    }

    /**
     * Moves to the next page.
     */
    public boolean nextPage() {
        List<MenuItem> data = getPageMenuItems();

        if (data == null || data.isEmpty()) {
            return false;
        }

        int maxPage = (data.size() - 1) / getMaxItemsPerPage();

        if (page >= maxPage) {
            return false;
        }

        page++;

        paginatedItems.clear();

        clearPaginatedItems(inventory, getRows());
        setPaginatedMenuItems();

        return true;
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }

    public int getCurrentPage() {
        return page;
    }

    public int getTotalPages() {
        List<MenuItem> data = getPageMenuItems();

        if (data == null || data.isEmpty()) {
            return 0;
        }

        return (data.size() + getMaxItemsPerPage() - 1)
                / getMaxItemsPerPage();
    }
}
