package nl.bryansuk.foundationapi.common.dependencies;

public interface Dependency<T> {
    String getName();
    T createInstance();
}
