package com.example.play_6sem.ui.modules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.play_6sem.ui.GlassCard

@Composable
fun LevelTopBar(
    title: String,
    timerText: String?,
    onPause: () -> Unit,
    onReset: () -> Unit
) {
    GlassCard {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(title, style = MaterialTheme.typography.titleLarge)
                if (timerText != null) {
                    Text(timerText)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onReset) { ButtonLabel("Сброс") }
                Button(onClick = onPause) { ButtonLabel("Пауза") }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun PauseMenu(
    onContinue: () -> Unit,
    onReset: () -> Unit,
    onExit: () -> Unit
) {
    GlassCard {
        Text("Пауза", style = MaterialTheme.typography.headlineSmall)
        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onContinue, modifier = Modifier.heightIn(min = 44.dp)) { ButtonLabel("Продолжить") }
            OutlinedButton(onClick = onReset, modifier = Modifier.heightIn(min = 44.dp)) { ButtonLabel("Сбросить уровень") }
            OutlinedButton(onClick = onExit, modifier = Modifier.heightIn(min = 44.dp)) { ButtonLabel("Выйти в меню") }
        }
    }
}

@Composable
fun ButtonLabel(text: String, singleLine: Boolean = false) {
    Text(
        text = text,
        maxLines = if (singleLine) 1 else 2,
        softWrap = !singleLine,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center
    )
}
