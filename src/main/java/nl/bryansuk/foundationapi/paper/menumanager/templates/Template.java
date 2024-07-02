package nl.bryansuk.foundationapi.paper.menumanager.templates;


import nl.bryansuk.foundationapi.paper.menumanager.menuitems.MenuItem;

import java.util.Map;

public record Template(String identifier, Map<Integer, MenuItem> getMenuItems) {}
