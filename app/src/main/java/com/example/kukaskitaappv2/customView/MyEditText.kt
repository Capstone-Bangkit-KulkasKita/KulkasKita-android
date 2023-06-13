package com.example.kukaskitaappv2.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.kukaskitaappv2.R

class MyEditText : AppCompatEditText {
    private lateinit var editTextBackground: Drawable
    private lateinit var editTextErrorBackground: Drawable
    private var isError = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        textSize = 14f
        background = if (isError) editTextErrorBackground else editTextBackground
    }

    private fun init() {
        editTextBackground = ContextCompat.getDrawable(context, R.drawable.bg_edit_text) as Drawable
        editTextErrorBackground = ContextCompat.getDrawable(context, R.drawable.bg_et_error) as Drawable

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (text.isNullOrEmpty()) {
                    error = resources.getString(R.string.field_cannot_empty)
                    isError = true
                }
            }
        })
    }

}