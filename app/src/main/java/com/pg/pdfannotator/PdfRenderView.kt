package com.pg.pdfannotator

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfRenderer
import android.util.Log
import androidx.compose.ui.geometry.Size


class PdfRendererView(context: Context) : androidx.appcompat.widget.AppCompatImageView(context) {

//    private var zoomable: Zoomable
//    private var scrollable: Scrollable


    private var canvas: Canvas? = null

    private var scale: Float = 1f
    private var dx: Float = 0f
    private var dy: Float = 0f
    private var size: Size? = null


    init {
//        zoomable = ZoomableImpl().apply {
//            register(context = context, this@PdfRendererView)
//        }
//        scrollable = ScrollableImpl().apply {
//            register(context = context, this@PdfRendererView)
//        }
    }


    fun updateScale(scale: Float, dx: Float, dy: Float) {
        this.scale = scale
        this.dx += dx
        this.dy += dy
        invalidate()
    }

    fun getSize(): Size? {
        return size
    }


    fun loadPdfFile(filePath: String, pdfRenderer: PdfRenderer, pageIndex: Int) {
//        val fileDescriptor =
//            ParcelFileDescriptor.open(File(filePath), ParcelFileDescriptor.MODE_READ_ONLY)
        openPage(pageIndex = pageIndex, pdfRenderer)
//        fileDescriptor.close()
    }

    private fun openPage(pageIndex: Int, pdfRenderer: PdfRenderer) {
        val page = pdfRenderer.openPage(pageIndex)
        size = Size(page.width.toFloat(), page.height.toFloat())
        val bitmap =
            Bitmap.createBitmap(
                page.width,
                page.height,
                Bitmap.Config.ARGB_8888
            )

//        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, )

        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        setImageBitmap(bitmap)
        adjustViewBounds = true
        page.close()
//        zoomable.updateImageMatrix()
    }

//    fun getImageAspectRatio(): Float {
//
//        return (currentPage?.width?.toFloat() ?: 1f) / (currentPage?.height?.toFloat() ?: 1f)
//    }

//    fun closePdfRenderer() {
//        currentPage?.close()
//    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        this.canvas = canvas
        Log.e("size, before", canvas.height.toString())
        canvas.scale(scale, scale)
        Log.e("size, after", canvas.height.toString())
        canvas.translate(dx, dy)
//        this.canvas = canvas
//        drawAnnotations()
    }


    private fun drawAnnotations() {
        // Ensure the canvas and current page are not null

//        GlobalScope.launch() {
//            canvas?.let { canvas ->
//                currentPage?.let { page ->
//                    // Set up the paint for drawing
//                    val paint = Paint().apply {
//                        color = Color.RED
//                        strokeWidth = 5f
//                        style = Paint.Style.STROKE
//                    }
//
//                    // Draw a rectangle as an example annotation
//                    val rect = RectF(100f, 100f, 200f, 200f)
//                    canvas.drawRect(rect, paint)
//                    canvas.translate(-scrollX, -scrollY)
//
//                    // Call invalidate to trigger a redraw
////                invalidate()
//                }
//
//            }
//        }

    }


}