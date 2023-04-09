package com.example.coursework

import android.view.animation.AlphaAnimation
import android.view.animation.LayoutAnimationController
import android.view.animation.RotateAnimation

interface OnAnimationsDataReceivedListener {

    fun rotateInit(
        rotate: RotateAnimation,
        rotateBack: RotateAnimation,
        addButtonRotate: RotateAnimation,
        addButtonBackRotation: RotateAnimation
    )

    fun alphaInit(alphaIn: AlphaAnimation, alphaOut: AlphaAnimation)

    fun layoutAnimationInit(controller: LayoutAnimationController)

}