package net.astralounge.foundationapi.common.manager;

import net.astralounge.foundationapi.common.exceptions.CircularDependencyException;
import net.astralounge.foundationapi.common.plugin.interfaces.FoundationDefaultPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommonManagerLoader {

    private final FoundationDefaultPlugin<?> plugin;

    private List<CommonManager> managers;
    private final List<CommonManager> loadOrder;
    public CommonManagerLoader(FoundationDefaultPlugin<?> plugin) {
        this.plugin = plugin;
        this.loadOrder = new ArrayList<>();
    }

    public List<CommonManager> getManagers() {
        if (managers == null) {
            List<CommonManager> list = new ArrayList<>(List.of(
                    plugin.getStartupManager(),
                    plugin.getFileManager(),
                    plugin.getInternalMessageManager(),
                    plugin.getDependencyManager()));
            list.addAll(plugin.getAdditionalManagers());
            managers = list;
        }
        return managers;
    }

    public void loadManagers() {
        plugin.getFoundationPluginLogger().debug("Loading Common Managers...");

        resolveLoadOrder(getManagers());

        for (CommonManager manager : loadOrder) {
            plugin.getFoundationPluginLogger().debug("Loading Manager: {}", manager.getClass().getSimpleName());
            manager.load();
        }
    }

    public void enableManagers() {
        for (CommonManager manager : loadOrder) {
            plugin.getFoundationPluginLogger().debug("enabling Manager: {}", manager.getClass().getSimpleName());
            manager.enable();
        }
    }

    public void disableManagers() {
        for (CommonManager manager : loadOrder.reversed()) {
            plugin.getFoundationPluginLogger().debug("disabling Manager: {}", manager.getClass().getSimpleName());
            manager.disable();
        }
    }

    private void resolveLoadOrder(@NotNull List<CommonManager> commonManagers) {
        Map<Class<? extends CommonManager>, CommonManager> managerMap = new HashMap<>();
        Map<Class<? extends CommonManager>, VisitState> visitStateMap = new HashMap<>();

        for (CommonManager manager : commonManagers) {
            managerMap.put(manager.getClass(), manager);
            visitStateMap.put(manager.getClass(), VisitState.UNVISITED);
        }

        for (CommonManager manager : commonManagers) {
            if (visitStateMap.get(manager.getClass()) == VisitState.UNVISITED) {
                depthFirstSearch(manager, managerMap, visitStateMap, new ArrayDeque<>());
            }
        }

        plugin.getFoundationPluginLogger().debug("Load Order: {}", loadOrder);
    }

    private void depthFirstSearch(CommonManager manager,
                                  Map<Class<? extends CommonManager>, CommonManager> managerMap,
                                  Map<Class<? extends CommonManager>, VisitState> visitStateMap,
                                  Deque<Class<? extends CommonManager>> path) {
        Class<? extends CommonManager> type = manager.getClass();

        visitStateMap.put(type, VisitState.VISITING);
        path.push(type);

        for (Class<? extends CommonManager> dependency : manager.getCommonDependencies()) {

            CommonManager dependencyManager = managerMap.get(dependency);
            if (dependencyManager == null) {
                throw new IllegalStateException(
                        "Missing dependency: " + dependency.getName() +
                                " required by " + type.getName()
                );
            }

            VisitState state = visitStateMap.get(dependency);
            if (state == VisitState.VISITING) {
                throw new CircularDependencyException(
                        "Circular dependency detected: " + formatCycle(dependency, path)
                );
            }

            if (state == VisitState.UNVISITED) {
                depthFirstSearch(dependencyManager, managerMap, visitStateMap, path);
            }
        }

        path.pop();
        visitStateMap.put(type, VisitState.VISITED);
        loadOrder.add(manager); // add AFTER dependencies
    }

    private static String formatCycle(Class<? extends CommonManager> repeated,
                                      Deque<Class<? extends CommonManager>> path) {
        StringBuilder sb = new StringBuilder();
        Iterator<Class<? extends CommonManager>> it = path.descendingIterator();
        boolean inCycle = false;

        while (it.hasNext()) {
            Class<? extends CommonManager> cls = it.next();
            if (cls.equals(repeated)) {
                inCycle = true;
            }
            if (inCycle) {
                sb.append(cls.getSimpleName()).append(" -> ");
            }
        }
        sb.append(repeated.getSimpleName());
        return sb.toString();
    }


    private enum VisitState {
        UNVISITED, VISITING, VISITED
    }

    private void clear() {
        loadOrder.clear();
    }

}
