package com.vvv.shootinggame.mastergame;

import com.vvv.shootinggame.R;

public enum BubbleColors {
    RED,
    YELLOW,
    BLUE,
    BLANK;

    public int getImageResId() {
        switch (this) {
            case BLUE:
                return R.drawable.blue;
            case RED:
                return R.drawable.red;
            case YELLOW:
                return R.drawable.orange;
            case BLANK:
                return R.drawable.transparent;
        }
        return 0;
    }
}
