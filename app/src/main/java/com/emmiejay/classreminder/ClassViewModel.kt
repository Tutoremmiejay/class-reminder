package com.emmiejay.classreminder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClassViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = ClassDatabase.getInstance(application).classDao()
    private val appContext = application.applicationContext

    val classes: StateFlow<List<ClassEntry>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addClass(dayOfWeek: Int, hour: Int, minute: Int, studentName: String) {
        viewModelScope.launch {
            val id = dao.insert(ClassEntry(dayOfWeek = dayOfWeek, hour = hour, minute = minute, studentName = studentName))
            val entry = dao.getById(id.toInt()) ?: return@launch
            AlarmScheduler.schedule(appContext, entry)
        }
    }

    fun updateClass(entry: ClassEntry) {
        viewModelScope.launch {
            AlarmScheduler.cancel(appContext, entry)
            dao.update(entry)
            AlarmScheduler.schedule(appContext, entry)
        }
    }

    fun deleteClass(entry: ClassEntry) {
        viewModelScope.launch {
            AlarmScheduler.cancel(appContext, entry)
            dao.delete(entry)
        }
    }
}
