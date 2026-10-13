package id.ac.mulia.tugasku.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mataKuliah: String,
    val judul: String,
    val deskripsi: String,
    val deadline: String,   // format ISO yyyy-MM-dd (urut alfabet = urut tanggal)
    val prioritas: String,  // Rendah / Sedang / Tinggi
    val status: String      // Belum / Proses / Selesai
)

object Status { val all = listOf("Belum", "Proses", "Selesai") }
object Prioritas { val all = listOf("Rendah", "Sedang", "Tinggi") }

/** Sisa hari menuju deadline (negatif = terlambat), null jika format salah. */
fun Task.daysLeft(): Long? = runCatching {
    ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(deadline))
}.getOrNull()
