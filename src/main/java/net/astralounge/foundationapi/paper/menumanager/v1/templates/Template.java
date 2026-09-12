package net.astralounge.foundationapi.paper.menumanager.v1.templates;


import net.astralounge.foundationapi.paper.menumanager.v1.menuitems.MenuItem;

import java.util.Map;

public record Template(String identifier, Map<Integer, MenuItem> getMenuItems) {}