package com.app.ngila.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

public class AnimationUtils {
    public static void PulseAnimation(View view){
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleDown.setDuration(310);
        scaleDown.setInterpolator(new FastOutSlowInInterpolator());
        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

        scaleDown.start();
    }
    public static void UpAnimation(final View from, final View to){
        to.setAlpha(0);
        to.setVisibility(View.VISIBLE);
        to.setTranslationY(from.getHeight());
        AnimatorSet ani= AnimationUtils.createTogetherSet( AnimationUtils.getObjectAni(
                to,"translationY",450,new DecelerateInterpolator(2),
                0
        ),AnimationUtils.getObjectAni(
                to,"alpha",250,null,
                1
        ),AnimationUtils.getObjectAni(
                from,"alpha",250,null,
                0
        ));
        ani.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                from .setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        ani.start();

    }
    public static ObjectAnimator getObjectAniRepeat(Object target, String propertyName, long duration, TimeInterpolator interpolation, float... values){
        ObjectAnimator ob= ObjectAnimator.ofFloat(target, propertyName,values);
        ob.setDuration(duration);
        ob.setInterpolator(interpolation);
        ob.setRepeatCount(ObjectAnimator.INFINITE);
        ob.setRepeatMode(ObjectAnimator.REVERSE);
        return ob;
    }
    public static ObjectAnimator getObjectAni(Object target, String propertyName, long duration, TimeInterpolator interpolation, float... values){
        ObjectAnimator ob= ObjectAnimator.ofFloat(target, propertyName,values);
        ob.setDuration(duration);
        ob.setInterpolator(interpolation);
        return ob;
    }
    public static ObjectAnimator getObjectAniOfInt(Object target, String propertyName, long duration, TimeInterpolator interpolation, int... values){
        ObjectAnimator ob= ObjectAnimator.ofInt(target, propertyName,values);
        ob.setDuration(duration);
        ob.setInterpolator(interpolation);
        return ob;
    }

    public static ValueAnimator getColorAni(final View target, int fromColor, int toColor, int duration){
        ValueAnimator anim= ValueAnimator.ofObject(new ArgbEvaluator(),fromColor,toColor);
        anim.setDuration(duration);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(target instanceof ImageView){
                    ((ImageView)target).setColorFilter((Integer) animation.getAnimatedValue());
                }
                else{

                    target.setBackgroundColor((Integer) animation.getAnimatedValue());
                }
            }});
        return anim;
    }
    public static ValueAnimator getBackgroundColorAni(final View target, int fromColor, int toColor, int duration){
        ValueAnimator anim= ValueAnimator.ofObject(new ArgbEvaluator(),fromColor,toColor);
        anim.setDuration(duration);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                target.setBackgroundColor((Integer) animation.getAnimatedValue());

            }});
        return anim;
    }
    public static ValueAnimator getTextColorAni(final TextView target, int fromColor, int toColor, int duration){
        ValueAnimator anim= ValueAnimator.ofObject(new ArgbEvaluator(),fromColor,toColor);
        anim.setDuration(duration);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                target.setTextColor((Integer) animation.getAnimatedValue());
            }});
        return anim;
    }
    public static AnimatorSet createTogetherSet(Animator... items){
        AnimatorSet firstSet= new AnimatorSet();
        firstSet.playTogether(items);
        return firstSet;
    }
    public static AnimatorSet createSeqSet(Animator... items){
        AnimatorSet firstSet= new AnimatorSet();
        firstSet.playSequentially(items);
        return firstSet;
    }

}
