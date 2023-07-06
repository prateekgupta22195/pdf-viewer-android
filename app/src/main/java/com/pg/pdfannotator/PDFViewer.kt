package com.pg.pdfannotator

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pg.pdfannotator.DimenUtil.Companion.pxToDp

private val minScaleFactor = 0.5f
private val maxScaleFactor = 5.0f

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PDFViewer(file: String, vm: PDFViewerVM = viewModel()) {

    val context: Context = LocalContext.current
    val editMode = remember {
        vm.editModeOn
    }
//
    val pdfRenderer = remember {
        val fileDescriptor = ParcelFileDescriptor.open(
            getFileFromAssets(context = context, "10840.pdf"), ParcelFileDescriptor.MODE_READ_ONLY
        )
        PdfRenderer(fileDescriptor!!)
    }
    val scale by remember { mutableStateOf(1f) }
    val offset by remember { mutableStateOf(Offset.Zero) }

    val configuration = LocalConfiguration.current
    val screenWidth = vm.screenWidth(localConfiguration = configuration, context = context)
    val screenHeight = vm.screenHeight(localConfiguration = configuration, context = context)

    Scaffold(topBar = {
        TopBarComposable(editMode.value) { change -> editMode.value = change }
    }, bottomBar = {
        if (editMode.value)
            DrawingPalette()
    }) { insetPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            userScrollEnabled = false
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


}

private fun createPathFromTouchPoints(touchPoints: List<Offset>): Path {
    val path = Path()
    if (touchPoints.isNotEmpty()) {
        path.moveTo(touchPoints.first().x, touchPoints.first().y)
        for (i in 1 until touchPoints.size) {
            Log.e("touchPoints", "createPathFromTouchPoints: ${touchPoints[i]}")

            path.lineTo(touchPoints[i].x, touchPoints[i].y)
        }
    }
    return path
}


