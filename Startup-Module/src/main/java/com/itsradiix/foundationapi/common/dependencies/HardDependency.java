package com.itsradiix.foundationapi.common.dependencies;

import com.itsradiix.foundationapi.common.startup.LoadError;
import com.itsradiix.foundationapi.common.startup.StartupDataManager;

import java.lang.reflect.InvocationTargetException;

public record HardDependency<T>(String name, Class<T> classType) implements Dependency<T>{

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T createInstance() {
        try {
            return classType.getConstructor().newInstance();
        } catch (InstantiationException |
                 IllegalAccessException |
                 NoSuchMethodException |
                 InvocationTargetException e) {
            StartupDataManager.getInstance().addLoadError(new LoadError(LoadError.Level.FATAL, "Unable to load dependency: " + name));
            return null;
        }
    }

}
