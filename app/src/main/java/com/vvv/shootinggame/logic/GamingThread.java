package com.vvv.shootinggame.logic;

public class GamingThread extends Thread {

    protected final GamingMachine mGamingMachine;
    private final Object mLock = new Object();
    private volatile boolean mIsGameRunning;
    private volatile boolean mIsGamePause;

    public GamingThread(GamingMachine gamingMachine) {
        mGamingMachine = gamingMachine;
        mIsGameRunning = false;
    }

    protected void doIt(long elapsedMillis) {
    }

    @Override
    public void start() {
        mIsGameRunning = true;
        mIsGamePause = false;
        super.start();
    }

    public void stopGame() {
        mIsGameRunning = false;
        resumeGame();
    }

    @Override
    public void run() {
        long elapsedMillis;
        long currentTimeMillis;
        long previousTimeMillis = System.currentTimeMillis();

        while (mIsGameRunning) {
            currentTimeMillis = System.currentTimeMillis();
            elapsedMillis = currentTimeMillis - previousTimeMillis;
            if (mIsGamePause) {
                while (mIsGamePause) {
                    try {
                        synchronized (mLock) {
                            mLock.wait();
                        }
                    } catch (InterruptedException e) {
                    }
                }
                currentTimeMillis = System.currentTimeMillis();
            }
            doIt(elapsedMillis);
            previousTimeMillis = currentTimeMillis;
        }
    }

    public void pauseGame() {
        mIsGamePause = true;
    }

    public void resumeGame() {
        if (mIsGamePause) {
            mIsGamePause = false;
            synchronized (mLock) {
                mLock.notify();
            }
        }
    }

    public boolean isGameRunning() {
        return mIsGameRunning;
    }

    public boolean isGamePaused() {
        return mIsGamePause;
    }

}
