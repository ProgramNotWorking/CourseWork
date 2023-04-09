package com.example.coursework

import android.content.Context
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.view.animation.RotateAnimation
import androidx.constraintlayout.widget.ConstraintLayout

class AnimationsHelperClass(val context: Context) {
    private var onAnimationsDataReceivedListener: OnAnimationsDataReceivedListener? = null

    fun setAnimationsDataReceivedListener(listener: OnAnimationsDataReceivedListener) {
        onAnimationsDataReceivedListener = listener

        onAnimationsDataReceivedListener?.layoutAnimationInit(
            getAnimationController()
        )

        onAnimationsDataReceivedListener?.rotateInit(
            getRotateAnimation(false),
            getRotateAnimation(true),
            getAddButtonRotation(false),
            getAddButtonRotation(true)
        )

        onAnimationsDataReceivedListener?.alphaInit(
            getAlphaAnimations(true),
            getAlphaAnimations(false)
        )
    }

    fun headersAnimationInit(headersList: ArrayList<ConstraintLayout>) {
        for (item in headersList) {
            item.layoutAnimation = getAnimationController()
            item.scheduleLayoutAnimation()
        }
    }

    private fun getAnimationController(): LayoutAnimationController {
        return AnimationUtils.loadLayoutAnimation(
            context, R.anim.recycler_view_fall_down_animation
        )
    }

    private fun getRotateAnimation(isBack: Boolean): RotateAnimation {
        val rotate: RotateAnimation

        if (!isBack) {
            rotate = RotateAnimation(
                180f,
                0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            rotate.duration = 500
        } else {
            rotate = RotateAnimation(
                -180f,
                0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            rotate.duration = 500
        }

        return rotate
    }

    private fun getAddButtonRotation(isBack: Boolean): RotateAnimation {
        val rotate: RotateAnimation

        if (!isBack) {
            rotate = RotateAnimation(
                90f,
                0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            rotate.duration = 500
        } else {
            rotate = RotateAnimation(
                0f,
                90f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            rotate.duration = 500
        }

        return rotate
    }

    private fun getAlphaAnimations(isAlphaIn: Boolean): AlphaAnimation {
        val alpha: AlphaAnimation

        if (isAlphaIn) {
            alpha = AlphaAnimation(0f, 1f)
            alpha.duration = 500
        } else {
            alpha = AlphaAnimation(1f, 0f)
            alpha.duration = 500
        }

        return alpha
    }

}