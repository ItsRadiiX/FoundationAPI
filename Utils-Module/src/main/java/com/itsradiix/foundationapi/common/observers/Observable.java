package com.itsradiix.foundationapi.common.observers;

public interface Observable {
    void addObserver(Observer o);
    void notifyObservers();
}
