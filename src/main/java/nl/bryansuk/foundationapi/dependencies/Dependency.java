package nl.bryansuk.foundationapi.dependencies;

public interface Dependency<T> {
    String getName();
    T createInstance();
}
