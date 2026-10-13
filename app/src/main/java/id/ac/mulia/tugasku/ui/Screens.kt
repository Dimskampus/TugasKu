@file:OptIn(ExperimentalMaterial3Api::class)

package id.ac.mulia.tugasku.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.ac.mulia.tugasku.data.Prioritas
import id.ac.mulia.tugasku.data.Status
import id.ac.mulia.tugasku.data.Task
import id.ac.mulia.tugasku.data.daysLeft

@Composable
private fun ChipRow(options: List<String>, selected: String?, onSelect: (String) -> Unit) {
    Row(
        Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { o ->
            FilterChip(selected = o == selected, onClick = { onSelect(o) }, label = { Text(o) })
        }
    }
}

@Composable
private fun BackButton(onBack: () -> Unit) =
    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali") }

// ===== 1. Daftar Tugas =====
@Composable
fun ListScreen(vm: TaskListViewModel, onAdd: () -> Unit, onOpen: (Long) -> Unit) {
    val s by vm.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Tugas Mahasiswa") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) { Icon(Icons.Default.Add, "Tambah tugas") }
        }
    ) { pad ->
        Column(Modifier.padding(pad).padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = s.query, onValueChange = vm::onQueryChange, singleLine = true,
                label = { Text("Cari judul / mata kuliah") }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(selected = s.filter == null, onClick = { vm.onFilterChange(null) }, label = { Text("Semua") })
                Status.all.forEach { st ->
                    FilterChip(selected = s.filter == st, onClick = { vm.onFilterChange(st) }, label = { Text(st) })
                }
            }
            Spacer(Modifier.height(8.dp))
            if (s.tasks.isEmpty()) {
                Text("Belum ada tugas. Tekan + untuk menambah.", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(s.tasks, key = { it.id }) { t -> TaskCard(t) { onOpen(t.id) } }
                }
            }
        }
    }
}

@Composable
private fun TaskCard(t: Task, onClick: () -> Unit) {
    val left = t.daysLeft()
    val urgent = t.status != "Selesai" && left != null && left <= 2
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(t.judul, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(t.mataKuliah, style = MaterialTheme.typography.bodyMedium)
            Text("Deadline: ${t.deadline}", style = MaterialTheme.typography.bodySmall)
            Text("Prioritas ${t.prioritas} • ${t.status}", style = MaterialTheme.typography.bodySmall)
            if (urgent) {
                Text(
                    if (left!! < 0) "Terlambat ${-left} hari!" else "Segera! Sisa $left hari",
                    color = Color(0xFFD32F2F), style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

// ===== 2. Detail Tugas =====
@Composable
fun DetailScreen(vm: TaskDetailViewModel, onBack: () -> Unit, onEdit: (Long) -> Unit) {
    val task by vm.task.collectAsStateWithLifecycle()
    var confirmDelete by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Detail Tugas") }, navigationIcon = { BackButton(onBack) })
    }) { pad ->
        val t = task
        if (t == null) {
            Box(Modifier.padding(pad).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tugas tidak ditemukan")
            }
        } else {
            Column(
                Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(t.judul, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Mata kuliah: ${t.mataKuliah}")
                Text("Deadline: ${t.deadline}")
                Text("Prioritas: ${t.prioritas}")
                Text(t.deskripsi.ifBlank { "(tanpa deskripsi)" })
                Text("Ubah status", style = MaterialTheme.typography.titleSmall)
                ChipRow(Status.all, t.status) { vm.setStatus(it) }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { onEdit(t.id) }) {
                        Icon(Icons.Default.Edit, null); Spacer(Modifier.width(6.dp)); Text("Edit")
                    }
                    OutlinedButton(onClick = { confirmDelete = true }) {
                        Icon(Icons.Default.Delete, null); Spacer(Modifier.width(6.dp)); Text("Hapus")
                    }
                }
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Hapus tugas?") },
            text = { Text("Tugas yang dihapus tidak dapat dikembalikan.") },
            confirmButton = {
                TextButton(onClick = { confirmDelete = false; vm.delete(onBack) }) { Text("Hapus") }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Batal") } }
        )
    }
}

// ===== 3 & 4. Form Tambah / Edit =====
@Composable
fun FormScreen(vm: TaskFormViewModel, onBack: () -> Unit) {
    val f = vm.form
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(if (vm.isEdit) "Edit Tugas" else "Tambah Tugas") },
            navigationIcon = { BackButton(onBack) }
        )
    }) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(f.mataKuliah, { v -> vm.update { it.copy(mataKuliah = v) } },
                label = { Text("Mata kuliah") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(f.judul, { v -> vm.update { it.copy(judul = v) } },
                label = { Text("Judul tugas") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(f.deskripsi, { v -> vm.update { it.copy(deskripsi = v) } },
                label = { Text("Deskripsi") }, minLines = 3, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(f.deadline, { v -> vm.update { it.copy(deadline = v) } },
                label = { Text("Deadline (yyyy-MM-dd)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Text("Prioritas", style = MaterialTheme.typography.titleSmall)
            ChipRow(Prioritas.all, f.prioritas) { p -> vm.update { it.copy(prioritas = p) } }
            Text("Status", style = MaterialTheme.typography.titleSmall)
            ChipRow(Status.all, f.status) { s -> vm.update { it.copy(status = s) } }
            f.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button(onClick = { vm.save(onBack) }, modifier = Modifier.fillMaxWidth()) { Text("Simpan") }
        }
    }
}
