package com.itsradiix.foundationapi.common.internalmessaging.message;

import com.itsradiix.foundationapi.common.internalmessaging.InternalMessageHandler;

public abstract class InternalMessage {

    private String name;
    private final boolean isAsynchronous;

    public InternalMessage(){
        this(false);
    }

    public InternalMessage(boolean async){
        this.name = getClass().getSimpleName();
        this.isAsynchronous = async;
    }

    public InternalMessage(String name, boolean async){
        this.name = name;
        this.isAsynchronous = async;
    }

    public abstract InternalMessageHandler getHandlers();

    public String getName() {
        return name;
    }

    public boolean isAsynchronous() {
        return isAsynchronous;
    }

    public void setName(String name) {
        this.name = name;
    }

}
