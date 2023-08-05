package com.vvv.shootinggame.key;

import android.view.MotionEvent;
import android.view.View;

import com.vvv.shootinggame.R;
import com.vvv.shootinggame.logic.GamingEvent;
import com.vvv.shootinggame.logic.GamingMachine;
import com.vvv.shootinggame.logic.InputTouchController;


public class TouchTouchController extends InputTouchController {

    private final GamingMachine mGamingMachine;

    public TouchTouchController(GamingMachine gamingMachine) {
        mGamingMachine = gamingMachine;
        gamingMachine.mActivity.findViewById(R.id.game_view).setOnTouchListener(new BasicOnTouchListener());
    }

    private class BasicOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                mAiming = true;
                mXDown = (int) event.getX();
                mYDown = (int) event.getY();
            } else if (action == MotionEvent.ACTION_MOVE) {
                mXDown = (int) event.getX();
                mYDown = (int) event.getY();
            } else if (action == MotionEvent.ACTION_UP) {
                mAiming = false;
                mXUp = (int) event.getX();
                mYUp = (int) event.getY();
                mGamingMachine.onGameEvent(GamingEvent.SHOOT);
            }

            return true;
        }
    }

}
