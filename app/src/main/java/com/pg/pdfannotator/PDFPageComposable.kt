package com.pg.pdfannotator

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.scale
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

@Composable
fun PDFPageComposable(index: Int, pdfRenderer: PdfRenderer, scale: Float) {

    var bitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    LaunchedEffect(Unit, block = {
        runBlocking(Dispatchers.Default + CoroutineExceptionHandler { _, exception ->
            Log.e("hello ", exception.message.toString())
        }) {
            Log.e("hello ", "inside launched effect ${Thread.currentThread().name}")
            val page = pdfRenderer.openPage(index)
            Log.e("page open ", "inside launched effect ${Thread.currentThread().name}")
            bitmap = Bitmap.createBitmap(
                ((page.width).toInt()),
                ((page.height).toInt()),
                Bitmap.Config.ARGB_8888
            )
            page.render(bitmap!!, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            Log.e("page closed ", "inside launched effect ${Thread.currentThread().name}")
        }

    })
    if (bitmap != null) Canvas(modifier = Modifier, onDraw = {
        scale(scale, scale) {
            drawImage(bitmap!!.asImageBitmap())
        }
    })


}