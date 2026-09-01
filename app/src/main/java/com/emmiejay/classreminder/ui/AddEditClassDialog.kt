package com.emmiejay.classreminder.ui

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emmiejay.classreminder.ClassEntry
import com.emmiejay.classreminder.DayNames
import com.emmiejay.classreminder.formatTime
import java.util.Calendar

@Composable
fun AddEditClassDialog(
    context: Context,
    existing: ClassEntry?,
    onDismiss: () -> Unit,
    onSave: (dayOfWeek: Int, hour: Int, minute: Int, studentName: String) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var studentName by remember { mutableStateOf(existing?.studentName ?: "") }
    var dayOfWeek by remember { mutableStateOf(existing?.dayOfWeek ?: Calendar.MONDAY) }
    var hour by remember { mutableStateOf(existing?.hour ?: 17) }
    var minute by remember { mutableStateOf(existing?.minute ?: 0) }
    var dayMenuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add class" else "Edit class") },
        text = {
            Column {
                OutlinedTextField(
                    value = studentName,
                    onValueChange = { studentName = it },
                    label = { Text("Student name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Column(modifier = Modifier.padding(top = 12.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = dayMenuExpanded,
                        onExpandedChange = { dayMenuExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = DayNames.name(dayOfWeek),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Day") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayMenuExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        DropdownMenu(
                            expanded = dayMenuExpanded,
                            onDismissRequest = { dayMenuExpanded = false }
                        ) {
                            DayNames.orderedForDisplay.forEach { day ->
                                DropdownMenuItem(
                                    text = { Text(DayNames.name(day)) },
                                    onClick = {
                                        dayOfWeek = day
                                        dayMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Column(modifier = Modifier.padding(top = 12.dp)) {
                    OutlinedTextField(
                        value = formatTime(hour, minute),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Class start time") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        trailingIcon = {
                            TextButton(onClick = {
                                TimePickerDialog(
                                    context,
                                    { _, h, m -> hour = h; minute = m },
                                    hour,
                                    minute,
                                    false
                                ).show()
                            }) { Text("Pick") }
                        }
                    )
                    Text("A reminder alarm fires 15 minutes before this time.")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (studentName.isNotBlank()) {
                    onSave(dayOfWeek, hour, minute, studentName.trim())
                }
            }) { Text("Save") }
        },
        dismissButton = {
            Column {
                TextButton(onClick = onDismiss) { Text("Cancel") }
                if (onDelete != null) {
                    TextButton(onClick = onDelete) { Text("Delete") }
                }
            }
        }
    )
}
