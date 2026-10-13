package id.ac.mulia.tugasku.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.createSavedStateHandle
import id.ac.mulia.tugasku.TugasApp
import id.ac.mulia.tugasku.data.Task
import id.ac.mulia.tugasku.data.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

// ---------- Factory ----------
private fun CreationExtras.repo(): TaskRepository =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TugasApp).repository

val AppViewModelFactory = viewModelFactory {
    initializer { TaskListViewModel(repo()) }
    initializer { TaskDetailViewModel(repo(), createSavedStateHandle()) }
    initializer { TaskFormViewModel(repo(), createSavedStateHandle()) }
}

// ---------- Daftar ----------
data class ListUiState(
    val tasks: List<Task> = emptyList(),
    val query: String = "",
    val filter: String? = null
)

class TaskListViewModel(private val repo: TaskRepository) : ViewModel() {
    private val query = MutableStateFlow("")
    private val filter = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ListUiState> = combine(repo.tasks, query, filter) { tasks, q, f ->
        ListUiState(
            tasks = tasks.filter { t ->
                (f == null || t.status == f) &&
                    (q.isBlank() || t.judul.contains(q, true) || t.mataKuliah.contains(q, true))
            },
            query = q,
            filter = f
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ListUiState())

    fun onQueryChange(q: String) { query.value = q }
    fun onFilterChange(f: String?) { filter.value = f }
}

// ---------- Detail ----------
class TaskDetailViewModel(
    private val repo: TaskRepository,
    handle: SavedStateHandle
) : ViewModel() {
    private val id: Long = handle.get<Long>("id") ?: -1L

    val task: StateFlow<Task?> = repo.observe(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun setStatus(status: String) = viewModelScope.launch {
        task.value?.let { repo.save(it.copy(status = status)) }
    }

    fun delete(onDone: () -> Unit) = viewModelScope.launch {
        task.value?.let { repo.delete(it) }
        onDone()
    }
}

// ---------- Form (tambah & edit) ----------
data class FormState(
    val mataKuliah: String = "",
    val judul: String = "",
    val deskripsi: String = "",
    val deadline: String = LocalDate.now().plusDays(7).toString(),
    val prioritas: String = "Sedang",
    val status: String = "Belum",
    val error: String? = null
)

class TaskFormViewModel(
    private val repo: TaskRepository,
    handle: SavedStateHandle
) : ViewModel() {
    private val id: Long = handle.get<Long>("id") ?: -1L
    val isEdit = id != -1L

    var form by mutableStateOf(FormState())
        private set

    init {
        if (isEdit) viewModelScope.launch {
            repo.get(id)?.let {
                form = FormState(it.mataKuliah, it.judul, it.deskripsi, it.deadline, it.prioritas, it.status)
            }
        }
    }

    fun update(transform: (FormState) -> FormState) { form = transform(form).copy(error = null) }

    fun save(onDone: () -> Unit) {
        val f = form
        val error = when {
            f.mataKuliah.isBlank() -> "Mata kuliah wajib diisi"
            f.judul.isBlank() -> "Judul wajib diisi"
            runCatching { LocalDate.parse(f.deadline) }.isFailure -> "Deadline harus berformat yyyy-MM-dd"
            else -> null
        }
        if (error != null) { form = f.copy(error = error); return }
        viewModelScope.launch {
            repo.save(
                Task(
                    id = if (isEdit) id else 0,
                    mataKuliah = f.mataKuliah.trim(), judul = f.judul.trim(),
                    deskripsi = f.deskripsi.trim(), deadline = f.deadline,
                    prioritas = f.prioritas, status = f.status
                )
            )
            onDone()
        }
    }
}
