package net.astralounge.foundationapi.common.exceptions;

import net.astralounge.foundationapi.common.manager.CommonManager;

public class ManagerNotYetLoadedException extends RuntimeException {
    public ManagerNotYetLoadedException(String message) {
        super(message);
    }

    public ManagerNotYetLoadedException(CommonManager commonManager) {
        super(commonManager.getClass().getSimpleName() + " is not yet loaded!");
    }
}
