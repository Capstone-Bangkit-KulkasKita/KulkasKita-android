package com.example.kukaskitaappv2.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.kukaskitaappv2.R

class MyButton: AppCompatButton {
    private lateinit var btnBackground : Drawable
    private var txtColor: Int = 0
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = btnBackground
        setTextColor(txtColor)
        textSize = 15f
        gravity = Gravity.CENTER
    }

    private fun init() {
        txtColor = ContextCompat.getColor(context, R.color.dark_green)
        btnBackground = ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
    }
}