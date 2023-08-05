package com.vvv.shootinggame.logic;

public class WrSulatThread extends GamingThread {
    private static final int SLEEP_TIME = 16;

    public WrSulatThread(GamingMachine gamingMachine) {
        super(gamingMachine);
    }

    protected void doIt(long elapsedMillis) {

        if (elapsedMillis < SLEEP_TIME) {
            try {
                Thread.sleep(SLEEP_TIME - elapsedMillis);
            } catch (InterruptedException e) {
            }
        }
        mGamingMachine.onDraw();
    }
}
