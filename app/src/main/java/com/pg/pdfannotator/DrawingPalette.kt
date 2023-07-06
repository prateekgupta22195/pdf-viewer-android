package com.pg.pdfannotator

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalTextApi::class)
@Composable
fun DrawingPalette() {

    val textMeasure = rememberTextMeasurer()

    val context = LocalContext.current

    Card(
        modifier = Modifier
            .height(72.dp)
            .padding(12.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scrollable(rememberScrollState(), Orientation.Horizontal),

            contentAlignment = Alignment.Center
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(48.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Canvas(modifier = Modifier.size(16.dp), onDraw = {

                            drawCircle(
                                center = Offset(0f, 40f),
                                color = Color.Red
                            )
                        })
                        Text(text = "MARKER")
                    }
                }

                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Canvas(modifier = Modifier.size(16.dp), onDraw = {
                            drawImage(
                                image = BitmapFactory.decodeResource(
                                    context.resources,
                                    android.R.drawable.ic_menu_edit
                                ).asImageBitmap(),

                                )
                        })
                        Text(text = "PENCIL")
                    }
                }
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Canvas(modifier = Modifier.size(16.dp), onDraw = {

                            val measuredText =
                                textMeasure.measure(
                                    AnnotatedString("T"),
                                    overflow = TextOverflow.Ellipsis,
                                    style = TextStyle(
                                        fontSize = 24.sp,
                                        color = Color.Blue,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )

                            drawText(measuredText)
                        })
                        Text(text = "TEXT")
                    }
                }

                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Canvas(modifier = Modifier.size(16.dp), onDraw = {
                            drawImage(
                                image = BitmapFactory.decodeResource(
                                    context.resources,
                                    android.R.drawable.ic_media_previous
                                ).asImageBitmap(),

                                )
                        })
                        Text(text = "UNDO")
                    }
                }

                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Canvas(modifier = Modifier.size(16.dp), onDraw = {
                            drawImage(
                                image = BitmapFactory.decodeResource(
                                    context.resources,
                                    android.R.drawable.editbox_background_normal
                                ).asImageBitmap(),

                                )
                        })
                        Text(text = "ERASER")
                    }
                }
            }
        }
    }
}