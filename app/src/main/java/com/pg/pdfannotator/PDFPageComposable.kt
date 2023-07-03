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
import androidx.compose.ui.text.ExperimentalTextApi
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalTextApi::class)
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

    if (bitmap != null)
        Canvas(modifier = Modifier) {
            scale(scale, scale) {
                drawImage(bitmap!!.asImageBitmap())
//                drawIntoCanvas { canvas ->
//                    val text = "Hello, World!"
//                    val textSize = 48f
//                    val textColor = Color.White
//                    val textPaint = Paint().apply {
//                        isAntiAlias = true
//                        color = android.graphics.Color.parseColor("#00ffff")
//                    }
//                    canvas.nativeCanvas.drawText(text, 100f, 100f, textPaint)
//                }
            }

        }

//    if (bitmap != null)
//        Spacer(
//            modifier = Modifier
//                .drawWithCache {
//
//                    val measuredText =
//                        textMeasurer.measure(
//                            AnnotatedString("hellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohellohello"),
//                            constraints = Constraints.fixed(
//                                width = (size.width / 3f).toInt(),
//                                height = (size.height / 3f).toInt()
//                            ),
//                            overflow = TextOverflow.Ellipsis,
//                            style = TextStyle(fontSize = 18.sp, color = Color.Red)
//                        )
//
//
//
//                    onDrawBehind {
//                        scale(scale, scale) {
//                            drawImage(bitmap!!.asImageBitmap())
//                            drawText(measuredText)
//                        }
//                    }
//                }
//        )
}