package com.itsradiix.foundationapi.paper.menumanager.templates;


import com.itsradiix.foundationapi.paper.menumanager.menuitems.MenuItem;

import java.util.Map;

public record Template(String identifier, Map<Integer, MenuItem> getMenuItems) {}