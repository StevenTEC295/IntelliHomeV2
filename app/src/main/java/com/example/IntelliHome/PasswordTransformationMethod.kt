package com.example.IntelliHome

import android.graphics.Rect
import android.text.method.PasswordTransformationMethod
import android.view.View

class SquarePasswordTransformationMethod : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence, view: View): CharSequence {
        return object : CharSequence {
            override val length: Int
                get() = source.length

            override fun get(index: Int): Char {
                // Devuelve un rombo en el texto
                return '◆'
            }

            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                return source.subSequence(startIndex, endIndex)
            }
        }
    }

    override fun onFocusChanged(view: View?, sourceText: CharSequence?, focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        // Do nothing extra here
    }
}