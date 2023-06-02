package com.jcrawley.remindme.view;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class AnimationHelper {



    public static void fadeOut(View view){
        if(view.getVisibility() == View.VISIBLE){
            fadeOut(view, ()->{});
        }
    }

    private static void fadeOut(View view, Runnable onComplete){
        runFadeAnimation(view, 1, 0, onComplete);
    }


    public static void fadeIn(View view){
        if(view.getVisibility() != View.VISIBLE){
            runFadeAnimation(view, 0, 1, ()-> {});
        }
    }


    public static void fadeOutAndIn(View view, Runnable inBetweenCode){
        fadeOut(view, ()->{
            inBetweenCode.run();
            fadeIn(view);
        });
    }


    private static void runFadeAnimation(View view, float initialAlpha, float endAlpha, Runnable onComplete){
        Animation animation = new AlphaAnimation(initialAlpha, endAlpha);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                int visibility = endAlpha == 0 ? View.INVISIBLE : View.VISIBLE;
                view.setVisibility(visibility);
                view.clearAnimation();
                onComplete.run();
            }

            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
        });
        view.clearAnimation();
        view.setAnimation(animation);
        animation.setStartOffset(100);
        animation.setDuration(500);
    }
}
