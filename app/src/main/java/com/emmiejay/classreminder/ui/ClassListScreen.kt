package com.emmiejay.classreminder.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emmiejay.classreminder.ClassEntry
import com.emmiejay.classreminder.ClassViewModel
import com.emmiejay.classreminder.DayNames
import com.emmiejay.classreminder.formatTime

private val Navy = Color(0xFF1B3A63)

@Composable
fun ClassListScreen(viewModel: ClassViewModel) {
    val classes by viewModel.classes.collectAsState()
    val context = LocalContext.current

    var showDialogFor by remember { mutableStateOf<ClassEntry?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Class Reminder by Emmiejay", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Navy,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = Navy) {
                Icon(Icons.Filled.Add, contentDescription = "Add class", tint = Color.White)
            }
        }
    ) { padding ->
        if (classes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No classes yet. Tap + to add one.")
            }
        } else {
            val grouped = DayNames.orderedForDisplay
                .mapNotNull { day -> classes.filter { it.dayOfWeek == day }.takeIf { it.isNotEmpty() }?.let { day to it } }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                grouped.forEach { (day, dayClasses) ->
                    item {
                        Text(
                            DayNames.name(day),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Navy)
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(dayClasses.sortedWith(compareBy({ it.hour }, { it.minute }))) { entry ->
                        ClassRow(entry = entry, onClick = { showDialogFor = entry })
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEditClassDialog(
            context = context,
            existing = null,
            onDismiss = { showAddDialog = false },
            onSave = { day, hour, minute, name ->
                viewModel.addClass(day, hour, minute, name)
                showAddDialog = false
            }
        )
    }

    showDialogFor?.let { entry ->
        AddEditClassDialog(
            context = context,
            existing = entry,
            onDismiss = { showDialogFor = null },
            onSave = { day, hour, minute, name ->
                viewModel.updateClass(entry.copy(dayOfWeek = day, hour = hour, minute = minute, studentName = name))
                showDialogFor = null
            },
            onDelete = {
                viewModel.deleteClass(entry)
                showDialogFor = null
            }
        )
    }
}

@Composable
private fun ClassRow(entry: ClassEntry, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(entry.studentName, fontWeight = FontWeight.SemiBold)
                Text(formatTime(entry.hour, entry.minute), color = Color.Gray)
            }
            Icon(Icons.Filled.Notifications, contentDescription = null, tint = Navy)
        }
    }
}
