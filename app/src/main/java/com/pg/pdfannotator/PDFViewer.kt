package com.pg.pdfannotator

import android.content.Context
import android.content.res.Resources
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val minScaleFactor = 0.5f
private val maxScaleFactor = 5.0f

@Composable
fun PDFViewer(file: String) {

    val context: Context = LocalContext.current
//
    val pdfRenderer = remember {
        val fileDescriptor =
            ParcelFileDescriptor.open(
                getFileFromAssets(context = context, "10840.pdf"),
                ParcelFileDescriptor.MODE_READ_ONLY
            )
        PdfRenderer(fileDescriptor!!)
    }

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val screenWidth =
        dpToPx(context = context, dp = LocalConfiguration.current.screenWidthDp.toFloat())
    val screenHeight =
        dpToPx(context = context, dp = LocalConfiguration.current.screenHeightDp.toFloat())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { offSet, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(minScaleFactor, maxScaleFactor)
                    offset += pan

                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        items(pdfRenderer.pageCount) {
            val pdfRendererView = remember {
                PdfRendererView(context).apply {
                    loadPdfFile(file, pdfRenderer, it)
                }
            }

            val limitOffset =
                if (pdfRendererView.getSize()!!.width * scale < screenWidth) {
                    Offset(
                        x = 0f,
                        y = 0f
                    )
                } else {
                    val maxOffsetX =
                        Math.abs(pdfRendererView.getSize()!!.width * scale - screenWidth)
                    val maxOffsetY =
                        Math.abs(pdfRendererView.getSize()!!.height * scale - screenHeight)
                    Offset(
                        x = offset.x.coerceIn(-maxOffsetX, 0f),
                        y = offset.y.coerceIn(-maxOffsetY, maxOffsetY)
                    )
                }



            Box(
                modifier = Modifier
                    .size(
                        pxToDp(pdfRendererView.getSize()!!.width * scale).dp,
                        pxToDp(pdfRendererView.getSize()!!.height * scale).dp
                    )
                    .offset(x = pxToDp(limitOffset.x).dp)
            ) {
                PDFPageComposable(
                    index = it,
                    pdfRenderer = pdfRenderer,
                    scale = scale,
                )
            }
        }
    }
}


fun pxToDp(px: Float): Float {
    return (px / Resources.getSystem().displayMetrics.density)
}


@Composable
fun TransformableSample() {
    // set up all transformation states
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }
    Box(
        Modifier
            // apply other transformations like rotation and zoom
            // on the pizza slice emoji
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation,
                translationX = offset.x,
                translationY = offset.y
            )
            // add transformable to listen to multitouch transformation events
            // after offset
            .transformable(state = state)
            .height(400.dp)
            .width(200.dp)
    )
}

fun dpToPx(dp: Float, context: Context): Float {
    val density = context.resources.displayMetrics.density
    return dp * density
}