package com.example.socialtalk.Utils

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.example.socialtalk.ui.theme.blusih

@Composable
fun TriggerProgressBar() {
    Dialog(onDismissRequest = { }) {
        CircularProgressIndicator(color = blusih)
    }
}


