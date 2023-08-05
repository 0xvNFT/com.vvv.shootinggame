package com.vvv.shootinggame.logic;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class GamingViews extends View {

    private ArrayList<ArrayList<GamingObject>> mLayers;

    public GamingViews(Context context) {
        super(context);
    }

    public GamingViews(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GamingViews(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setGameObjects(ArrayList<ArrayList<GamingObject>> gameObjects) {
        mLayers = gameObjects;
    }

    public void draw() {
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (mLayers) {
            int numLayers = mLayers.size();
            for (int i = 0; i < numLayers; i++) {
                ArrayList<GamingObject> currentLayer = mLayers.get(i);
                int numObjects = currentLayer.size();
                for (int j = 0; j < numObjects; j++) {
                    currentLayer.get(j).onDraw(canvas);
                }
            }
        }
    }

}
