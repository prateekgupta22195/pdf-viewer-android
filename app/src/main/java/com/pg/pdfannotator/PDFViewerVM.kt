package com.pg.pdfannotator

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.pg.pdfannotator.DimenUtil.Companion.dpToPx

class PDFViewerVM : ViewModel() {

    val editModeOn = mutableStateOf(false)

    fun screenWidth(localConfiguration: Configuration, context: Context) =
        dpToPx(context = context, dp = localConfiguration.screenWidthDp.toFloat())

    fun screenHeight(localConfiguration: Configuration, context: Context) =
        dpToPx(context = context, dp = localConfiguration.screenHeightDp.toFloat())


}
