//나침반 코드 참고
//출처 : https://copycoding.tistory.com/tag/TYPE_ACCELEROMETER
package com.gachon.userapp;

import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class RotateAnimationHelper {
    private ImageView pointer;
    private RelativeLayout pointerContainer;
    private float containerWidth, containerHeight;
    private float pivotX, pivotY;

    public RotateAnimationHelper(ImageView pointer, RelativeLayout pointerContainer) {
        this.pointer = pointer;
        this.pointerContainer = pointerContainer;
        initialize();
    }

    private void initialize() {
        pointerContainer.post(new Runnable() {
            @Override
            public void run() {
                containerWidth = pointerContainer.getWidth();
                containerHeight = pointerContainer.getHeight();
                pivotX = pointer.getX();
                pivotY = pointer.getY();
            }
        });
    }

    public void rotate(float mCurrentDegree, float azimuthunDegress, int duration) {
        RotateAnimation ra = new RotateAnimation(
                mCurrentDegree,
                -azimuthunDegress,
                Animation.RELATIVE_TO_SELF, pivotX / containerWidth,
                Animation.RELATIVE_TO_SELF, pivotY / containerHeight
        );
        ra.setDuration(duration);
        ra.setFillAfter(true);
        pointer.startAnimation(ra);
    }
}