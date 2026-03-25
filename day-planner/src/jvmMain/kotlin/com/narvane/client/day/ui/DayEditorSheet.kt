package com.narvane.client.day.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.narvane.client.day.logic.formatMinute
import com.narvane.client.day.model.DayAction
import com.narvane.client.day.model.DayEditor
import com.narvane.client.day.model.DaySelection
import kotlin.math.max
import kotlin.math.min

@Composable
internal fun DayEditorSheet(
    editor: DayEditor,
    selection: DaySelection,
    isEditingExisting: Boolean,
    onAction: (DayAction) -> Unit,
) {
    val start = min(selection.startMinute, selection.endMinute)
    val end = max(selection.startMinute, selection.endMinute)

    Column(
        modifier = Modifier.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "Adicionar titulo",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "${formatMinute(start)} - ${formatMinute(end)}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = editor.title,
            onValueChange = { onAction(DayAction.UpdateTitle(it)) },
            label = { Text("Titulo do evento") },
            singleLine = true,
        )
        editor.error?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (isEditingExisting) {
                TextButton(onClick = { onAction(DayAction.RemoveEvent) }) {
                    Text("Remover")
                }
            } else {
                TextButton(onClick = { onAction(DayAction.DismissEditor) }) {
                    Text("Cancelar")
                }
            }

            Row(horizontalArrangement = Arrangement.End) {
                if (isEditingExisting) {
                    TextButton(onClick = { onAction(DayAction.DismissEditor) }) {
                        Text("Fechar")
                    }
                }
                Button(
                    modifier = Modifier.padding(start = 8.dp),
                    onClick = { onAction(DayAction.SaveEvent) },
                    enabled = editor.title.isNotBlank(),
                ) {
                    Text("Salvar")
                }
            }
        }
    }
}
