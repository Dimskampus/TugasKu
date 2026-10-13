package id.ac.mulia.tugasku

import android.app.Application
import id.ac.mulia.tugasku.data.AppDatabase
import id.ac.mulia.tugasku.data.TaskRepository

class TugasApp : Application() {
    val repository by lazy { TaskRepository(AppDatabase.get(this).taskDao()) }
}
