package com.vvv.shootinggame.mastergame;

import android.annotation.SuppressLint;

import com.vvv.shootinggame.logic.Coke;
import com.vvv.shootinggame.logic.GamingMachine;

import java.util.ArrayList;

public class Bubbles extends Coke {
    public final int mRow, mCol;
    public final ArrayList<Bubbles> mEdges = new ArrayList<>(6);
    public BubbleColors mBubbleColors;
    public int mDepth = -1;
    public boolean mDiscover = false;

    protected Bubbles(GamingMachine gamingMachine, int row, int col, BubbleColors bubbleColors) {
        super(gamingMachine, bubbleColors.getImageResId());
        mRow = row;
        mCol = col;
        mBubbleColors = bubbleColors;
    }

    @Override
    public void startGame() {
    }

    @Override
    public void onUpdate(long elapsedMillis, GamingMachine gamingMachine) {
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setBubbleColor(BubbleColors color) {
        mBubbleColors = color;
        mBitmap = getDefaultBitmap(mResources.getDrawable(color.getImageResId()));
    }

    public void setBlankBubble() {
        setBubbleColor(BubbleColors.BLANK);
    }

}
