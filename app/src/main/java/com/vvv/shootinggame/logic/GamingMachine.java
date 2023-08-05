package com.vvv.shootinggame.logic;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;

public class GamingMachine {
    public final Activity mActivity;
    public final float mPixelFactor;
    public final int mScreenWidth;
    public final int mScreenHeight;
    private final ArrayList<ArrayList<GamingObject>> mLayers = new ArrayList<ArrayList<GamingObject>>();
    private final ArrayList<GamingObject> mGamingObjects = new ArrayList<>();
    private final ArrayList<GamingObject> mObjectsToAdd = new ArrayList<>();
    private final ArrayList<GamingObject> mObjectsToRemove = new ArrayList<>();
    private final GamingViews mGamingViews;
    public InputTouchController mInputTouchController;
    private GamingUpdateThread mGamingUpdateThread;
    private WrSulatThread mWrSulatThread;

    public GamingMachine(Activity activity, GamingViews gamingViews) {
        mActivity = activity;
        mGamingViews = gamingViews;
        mGamingViews.setGameObjects(mLayers);

        mScreenWidth = gamingViews.getWidth() - gamingViews.getPaddingRight() - gamingViews.getPaddingLeft();
        mScreenHeight = gamingViews.getHeight() - gamingViews.getPaddingTop() - gamingViews.getPaddingBottom();
        mPixelFactor = mScreenWidth / 2000f;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public void setInputController(InputTouchController inputTouchController) {
        mInputTouchController = inputTouchController;
    }

    public void startGame() {
        stopGame();

        int numGameObjects = mGamingObjects.size();
        for (int i = 0; i < numGameObjects; i++) {
            mGamingObjects.get(i).startGame();
        }

        if (mInputTouchController != null) {
            mInputTouchController.onStart();
        }

        mGamingUpdateThread = new GamingUpdateThread(this);
        mGamingUpdateThread.start();

        mWrSulatThread = new WrSulatThread(this);
        mWrSulatThread.start();
    }

    public void stopGame() {
        if (mGamingUpdateThread != null) {
            mGamingUpdateThread.stopGame();
            mGamingUpdateThread = null;
        }
        if (mWrSulatThread != null) {
            mWrSulatThread.stopGame();
        }
        if (mInputTouchController != null) {
            mInputTouchController.onStop();
        }
    }

    public void pauseGame() {
        if (mGamingUpdateThread != null) {
            mGamingUpdateThread.pauseGame();
        }
        if (mWrSulatThread != null) {
            mWrSulatThread.pauseGame();
        }
        if (mInputTouchController != null) {
            mInputTouchController.onPause();
        }
    }

    public void resumeGame() {
        if (mGamingUpdateThread != null) {
            mGamingUpdateThread.resumeGame();
        }
        if (mWrSulatThread != null) {
            mWrSulatThread.resumeGame();
        }
        if (mInputTouchController != null) {
            mInputTouchController.onResume();
        }
    }

    public boolean isRunning() {
        return mGamingUpdateThread != null && mGamingUpdateThread.isGameRunning();
    }

    public boolean isPaused() {
        return mGamingUpdateThread != null && mGamingUpdateThread.isGamePaused();
    }

    public void onUpdate(long elapsedMillis) {
        int numObjects = mGamingObjects.size();
        for (int i = 0; i < numObjects; i++) {
            mGamingObjects.get(i).onUpdate(elapsedMillis, this);
            mGamingObjects.get(i).onPostUpdate();
        }
        checkCollisions();
        synchronized (mLayers) {
            while (!mObjectsToRemove.isEmpty()) {
                GamingObject objectToRemove = mObjectsToRemove.remove(0);
                mGamingObjects.remove(objectToRemove);
                mLayers.get(objectToRemove.mLayer).remove(objectToRemove);
            }
            while (!mObjectsToAdd.isEmpty()) {
                GamingObject gamingObject = mObjectsToAdd.remove(0);
                addToLayerNow(gamingObject);
            }
        }
    }

    public void onDraw() {
        mGamingViews.draw();
    }

    private void checkCollisions() {
        int numGameObjects = mGamingObjects.size();
        for (int i = 0; i < numGameObjects; i++) {
            GamingObject objectA = mGamingObjects.get(i);
            for (int j = i + 1; j < numGameObjects; j++) {
                GamingObject objectB = mGamingObjects.get(j);
                if (objectA instanceof Coke && objectB instanceof Coke) {
                    Coke cokeA = (Coke) objectA;
                    Coke cokeB = (Coke) objectB;
                    if (cokeA.checkCollision(cokeB)) {
                        cokeA.onCollision(this, cokeB);
                        cokeB.onCollision(this, cokeA);
                    }
                }
            }
        }
    }

    private void addToLayerNow(GamingObject object) {
        int layer = object.mLayer;

        while (mLayers.size() <= layer) {
            mLayers.add(new ArrayList<GamingObject>());
        }
        mLayers.get(layer).add(object);
        mGamingObjects.add(object);
    }

    public void addGameObject(final GamingObject gamingObject, int layer) {
        gamingObject.mLayer = layer;
        if (isRunning()) {
            mObjectsToAdd.add(gamingObject);
        } else {
            addToLayerNow(gamingObject);
        }
    }

    public void removeGameObject(final GamingObject gamingObject) {
        mObjectsToRemove.add(gamingObject);
    }

    public void onGameEvent(GamingEvent gamingEvent) {
        int numObjects = mGamingObjects.size();
        for (int i = 0; i < numObjects; i++) {
            mGamingObjects.get(i).onGameEvent(gamingEvent);
        }
    }

    public Context getContext() {
        return mGamingViews.getContext();
    }

    public Activity getMainActivity() {
        return mActivity;
    }


}
