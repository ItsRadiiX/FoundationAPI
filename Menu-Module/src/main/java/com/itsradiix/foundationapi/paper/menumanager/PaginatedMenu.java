package com.itsradiix.foundationapi.paper.menumanager;


import com.itsradiix.foundationapi.paper.menumanager.menuitems.MenuItem;

import java.util.List;

public abstract class PaginatedMenu extends Menu {

    //Keep track of what page the menu is on
    protected int page = 0;
    //28 is max items because with the border set below,
    //28 empty slots are remaining.
    protected int maxItemsPerPage = 28;
    //the index represents the index of the slot
    //that the loop is on
    protected int index = 0;
    protected int slotIndex = 0;

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    /**
     * @return A list of the data being paginated. usually this is a list of items but it can be anything
     */
    public abstract List<MenuItem> getPageMenuItems();

    /**
     * Place each item in the paginated menu.
     * Automatically coded by default but override if you want custom functionality.
     * Calls the loopCode() method you define for each item returned in the getData() method
     */
    public void setPaginatedMenuItems() {

        List<MenuItem> data = getPageMenuItems();

        if (data != null && !data.isEmpty()) {
            for (int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;

                if (index >= data.size()) break;
                if (data.get(index) != null) {
                    setItem(index, data.get(index));
                }
            }
        }
    }

    /**
     * @return true if successful, false if already on the first page
     */
    public boolean prevPage() {
        if (page == 0) {
            return false;
        }

        page = page - 1;
        reloadItems();
        return true;
    }

    /**
     * @return true if successful, false if already on the last page
     */
    public boolean nextPage() {
        if (!((index + 1) >= getPageMenuItems().size())) {
            page = page + 1;
            reloadItems();
            return true;
        }

        return false;
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }

    public int getNextFreeSlot(){
        if (slotIndex == getSlots()) return -1;
        for (int i = 0; i < getSlots(); i++) {
            if (inventory.getItem(slotIndex) == null) return slotIndex;
            slotIndex++;
        }
        return -1;
    }
}
