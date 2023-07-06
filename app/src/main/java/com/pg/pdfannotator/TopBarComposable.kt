package com.pg.pdfannotator

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarComposable(editMode: Boolean, onChangeMode: (change: Boolean) -> Unit) {

    TopAppBar(title = {
        Text("PDF Annotator")
    }, actions = {
        if (!editMode) {
            IconButton(onClick = {
                onChangeMode(true)
            }) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_edit),
                    contentDescription = null
                )
            }
        } else

            Text(
                text = "Save",
                style = TextStyle(fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.clickable {
                    onChangeMode(false)
                })

    })
}