package com.pg.pdfannotator

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.pdf.PdfRenderer
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.Random

val cache = BitmapMemoryCache()

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

            val cachedBitmap: Bitmap? = cache.getBitmapFromMemoryCache(index.toString())
            if (cachedBitmap == null) {

                val timestamp = System.currentTimeMillis()
                val page = pdfRenderer.openPage(index)
                Log.e("page open ", "inside launched effect ${Thread.currentThread().name}")
                if (bitmap == null) {
                    bitmap = Bitmap.createBitmap(
                        ((page.width).toInt()),
                        ((page.height).toInt()),
                        Bitmap.Config.ARGB_8888
                    )
                } else {
                    bitmap!!.recycle()
                }
                Log.e("time ", "${System.currentTimeMillis() - timestamp}")
                page.render(bitmap!!, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                cache.addBitmapToMemoryCache(index.toString(), bitmap)
                page.close()
            } else {
                Log.e("returned ", "getBitmapFromMemoryCache: ")
                bitmap = cachedBitmap
            }

            Log.e("page closed ", "inside launched effect ${Thread.currentThread().name}")
        }

    })
    var touchPath = remember { generateRandomPath() }

    if (bitmap != null && !bitmap!!.isRecycled)
        Canvas(modifier = Modifier.pointerInput(Unit) {
        }) {
            scale(scale, scale) {
                drawImage(bitmap!!.asImageBitmap())

                drawIntoCanvas { canvas ->

                    if (index == 2) {
                        val text = "Hello, World!"
                        val textPaint = Paint().apply {
                            isAntiAlias = true
                            style = Paint.Style.FILL_AND_STROKE
                            strokeWidth = 1f
                            color = android.graphics.Color.parseColor("#00ffff")
                        }
                        canvas.nativeCanvas.drawText(text, 100f, 100f, textPaint)
                    }

                    if (index == 1) {
                        val linePaint = Paint().apply {
                            isAntiAlias = true
                            style = Paint.Style.FILL_AND_STROKE
                            strokeWidth = 4f
                            color = android.graphics.Color.parseColor("#ff00ff")
                        }
                        canvas.nativeCanvas.drawLine(200f, 508f, 270f, 508f, linePaint)

                    }

                    if (index == 3) {
                        drawPath(
                            path = touchPath,
                            color = Color.Green,
                            style = Stroke(width = 5f)
                        )
                    }

                }
            }

        }

    DisposableEffect(key1 = index) {
        onDispose {
            Log.e("dispose", "PDFPageComposable: ")
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


private fun generateRandomPath(): Path {
    val path = Path()

    // Set the starting point of the path
    val startX = 100f
    val startY = 100f
    path.moveTo(startX, startY)

    // Generate random points and add them to the path
    val random = Random()
    for (i in 0 until 10) {
        val x = startX + random.nextInt(200)
        val y = startY + random.nextInt(200)
        path.lineTo(x.toFloat(), y.toFloat())
    }

    return path
}