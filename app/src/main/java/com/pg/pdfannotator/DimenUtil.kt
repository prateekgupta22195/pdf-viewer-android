package com.pg.pdfannotator

import android.content.Context
import android.content.res.Resources

class DimenUtil {


    companion object {

        fun pxToDp(px: Float): Float {
            return (px / Resources.getSystem().displayMetrics.density)
        }

        fun dpToPx(dp: Float, context: Context): Float {
            val density = context.resources.displayMetrics.density
            return dp * density
        }
    }
}