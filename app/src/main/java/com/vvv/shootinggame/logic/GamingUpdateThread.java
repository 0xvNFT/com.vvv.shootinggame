package com.vvv.shootinggame.logic;

public class GamingUpdateThread extends GamingThread {
    public GamingUpdateThread(GamingMachine gamingMachine) {
        super(gamingMachine);
    }

    @Override
    protected void doIt(long elapsedMillis) {
        mGamingMachine.onUpdate(elapsedMillis);
    }
}
