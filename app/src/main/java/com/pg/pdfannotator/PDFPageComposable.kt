package com.pg.pdfannotator

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
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
    val touchPoints = remember { mutableStateListOf(Offset.Zero) }


    val paths = remember {
        mutableStateListOf<Path>()
    }
    var currentPath by remember {
        mutableStateOf(Path())
    }

    val pointerMo = Modifier.pointerInput(Unit) {

        detectDragGestures(onDragStart = { offset ->
            currentPath = Path()
            currentPath.moveTo(offset.x, offset.y)
            paths.add(currentPath)
            touchPoints.add(offset)
        }, onDrag = { change, dragAmount ->
            touchPoints.add(change.position)
//            Log.e("ondrag ", "inside launched effect ${Thread.currentThread().name}")
            currentPath.lineTo(change.position.x, change.position.y)
            paths.remove(currentPath)
            paths.add(currentPath)
        }, onDragEnd = {
            Log.e("end", "PDFPageComposable: ${touchPoints.size}")
        })


//        detectDragGestures(onDrag = { change, offset ->
//            Log.e("offset inside gesture", "PDFViewer: ${offset}")
////            val currentX = change.x
////            val currentY = offset.y
//            path.value.lineTo(change.position.x, change.position.y)
////            touchPoints.add(change.position)
////                        path.value.lineTo(currentX, currentY)
//        })
//        detectTapGestures(
//            onPress = { offset ->
//                path.value.moveTo(offset.x, offset.y)
////                touchPoints.add(offset)
//            }
//        )


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
                        page.width,
                        page.height,
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




    if (bitmap != null && !bitmap!!.isRecycled)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .then(pointerMo)
                .background(Color.Green)
        ) {
            scale(scale, scale) {
                drawImage(bitmap!!.asImageBitmap())


                drawIntoCanvas { canvas ->
                    Log.e("canvas ", "PDFViewer: $size")
                    val paint = Paint()
                    paint.color = Color.Red
                    paint.style = PaintingStyle.Stroke
                    paint.strokeWidth = 1f
//

                    drawContent(paths, currentPath)
//                    canvas.drawPath(
//                        currentPath
////                        createPathFromTouchPoints(touchPoints)
////                        Path().apply {
////
////                            lineTo(
////                                touchPoints[touchPoints.size - 1].x,
////                                touchPoints[touchPoints.size - 1].y
////                            )
////
////                        }
//                        ,
//                        paint = paint
//                    )
                }

                drawIntoCanvas { canvas ->

                    val svg =
                        generateSVG(SampleData.dataList, width = size.width, height = size.height)

                    if (index == 0) {
                        drawPath(
                            path = createPathFromTouchPoints(
                                simplifyCoordinates(
                                    SampleData.dataList,
                                    5f
                                )
                            ),
                            color = Color.Red,
                            style = Stroke(width = 2f)
                        )
                    }

//                    if (index == 2) {
//                        val text = "Hello, World!"
//                        val textPaint = Paint().apply {
//                            isAntiAlias = true
//                            style = Paint.Style.FILL_AND_STROKE
//                            strokeWidth = 1f
//                            color = android.graphics.Color.parseColor("#00ffff")
//                        }
//                        canvas.nativeCanvas.drawText(text, 100f, 100f, textPaint)
//                    }
//
//                    if (index == 1) {
//                        val linePaint = Paint().apply {
//                            isAntiAlias = true
//                            style = Paint.Style.FILL_AND_STROKE
//                            strokeWidth = 4f
//                            color = android.graphics.Color.parseColor("#ff00ff")
//                        }
//                        canvas.nativeCanvas.drawLine(200f, 508f, 270f, 508f, linePaint)
//
//                    }
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

private fun createPathFromTouchPoints(touchPoints: List<Offset>): Path {
    val path = Path()
    if (touchPoints.isNotEmpty()) {
        path.moveTo(touchPoints.first().x, touchPoints.first().y)
        for (i in 1 until touchPoints.size) {
            path.lineTo(touchPoints[i].x, touchPoints[i].y)
        }
    }
    return path
}


private fun DrawScope.drawContent(paths: List<Path>, currentPath: Path) {
    for (path in paths) {
        drawPath(
            path = path,
            color = Color.Red,
            alpha = 1f,
            style = Stroke(width = 1f)
        )
    }
    drawPath(
        path = currentPath,
        color = Color.Red,
        alpha = 1f,
        style = Stroke(width = 1f)
    )
}


fun bresenhamSampling(coordinates: List<Offset>): List<Offset> {
    val sampledPoints = mutableListOf<Offset>()

    for (i in 0 until coordinates.size - 1) {
        val start = coordinates[i]
        val end = coordinates[i + 1]

        val x0 = start.x.toInt()
        val y0 = start.y.toInt()
        val x1 = end.x.toInt()
        val y1 = end.y.toInt()

        val dx = kotlin.math.abs(x1 - x0)
        val dy = kotlin.math.abs(y1 - y0)
        val sx = if (x0 < x1) 1 else -1
        val sy = if (y0 < y1) 1 else -1
        var err = dx - dy

        var x = x0
        var y = y0

        while (true) {
            sampledPoints.add(Offset(x.toFloat(), y.toFloat()))

            if (x == x1 && y == y1) {
                break
            }

            val e2 = 2 * err
            if (e2 > -dy) {
                err -= dy
                x += sx
            }
            if (e2 < dx) {
                err += dx
                y += sy
            }
        }
    }

    return sampledPoints
}


fun generateSVG(coordinates: List<Offset>, width: Float, height: Float): String {
    val svgBuilder = StringBuilder()
    svgBuilder.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"$width\" height=\"$height\">\n")
    svgBuilder.append("<path d=\"")

    for (i in 0 until coordinates.size) {
        val coordinate = coordinates[i]
        val command = if (i == 0) "M" else "L"
        svgBuilder.append("$command${coordinate.x},${coordinate.y} ")
    }

    svgBuilder.append("\" stroke=\"black\" fill=\"none\"/>\n")
    svgBuilder.append("</svg>")

    return svgBuilder.toString()
}

fun simplifyCoordinates(coordinates: List<Offset>, tolerance: Float): List<Offset> {
    if (coordinates.size < 3) {
        return coordinates
    }

    val simplifiedPoints = mutableListOf<Offset>()

    // Find the point with the maximum distance
    var maxDistance = 0f
    var maxIndex = 0
    val start = coordinates.first()
    val end = coordinates.last()

    for (i in 1 until coordinates.size - 1) {
        val distance = perpendicularDistance(coordinates[i], start, end)
        if (distance > maxDistance) {
            maxDistance = distance
            maxIndex = i
        }
    }

    // If the max distance is greater than the tolerance, simplify recursively
    if (maxDistance > tolerance) {
        val left = coordinates.subList(0, maxIndex + 1)
        val right = coordinates.subList(maxIndex, coordinates.size)
        val leftResult = simplifyCoordinates(left, tolerance)
        val rightResult = simplifyCoordinates(right, tolerance)

        simplifiedPoints.addAll(leftResult.subList(0, leftResult.size - 1))
        simplifiedPoints.addAll(rightResult)
    } else {
        simplifiedPoints.add(start)
        simplifiedPoints.add(end)
    }

    return simplifiedPoints
}

fun perpendicularDistance(point: Offset, lineStart: Offset, lineEnd: Offset): Float {
    val dx = lineEnd.x - lineStart.x
    val dy = lineEnd.y - lineStart.y
    val denominator = dx * dx + dy * dy

    if (denominator == 0f) {
        return 0f
    }

    val t = ((point.x - lineStart.x) * dx + (point.y - lineStart.y) * dy) / denominator
    val nearestX = lineStart.x + t * dx
    val nearestY = lineStart.y + t * dy

    return distance(point.x, point.y, nearestX, nearestY)
}

fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
    val dx = x2 - x1
    val dy = y2 - y1
    return kotlin.math.sqrt(dx * dx + dy * dy)
}