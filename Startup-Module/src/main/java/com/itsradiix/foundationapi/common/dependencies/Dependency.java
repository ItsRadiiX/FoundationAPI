package com.itsradiix.foundationapi.common.dependencies;

public interface Dependency<T> {
    String getName();
    T createInstance();
}
