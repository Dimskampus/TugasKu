# Dokumen Rancangan & Alasan Teknis – TugasKu

Dokumen ini menjawab capaian praktikum 1 (analisis kebutuhan), 2 (rancangan alur, navigasi, model data, penyimpanan lokal), dan 4 (alasan teknis).

## 1. Analisis Kebutuhan
**Masalah:** tugas kuliah tersebar di chat, LMS, dan catatan pribadi sehingga mudah terlewat.
**Solusi:** satu aplikasi yang mencatat, memantau, dan memperbarui status tugas, termasuk saat tanpa internet.

| Kebutuhan | Penjelasan |
|---|---|
| Fungsional | Lihat daftar, tambah, lihat detail, ubah, hapus (dengan konfirmasi), ubah status |
| Non-fungsional | Berjalan offline, data tidak hilang saat aplikasi ditutup, tampilan sederhana di layar kecil, perubahan langsung terlihat di daftar |
| Opsional | Filter status, pencarian, indikator tenggat dekat |

## 2. Rancangan
### Alur pengguna
- Daftar Tugas → Tambah Tugas → Simpan → Daftar Tugas
- Daftar Tugas → Detail Tugas → Edit / Hapus → Daftar Tugas

### Struktur layar & navigasi
| Layar | Route | Fungsi |
|---|---|---|
| Daftar | `list` | Menampilkan tugas, cari, filter, tombol tambah |
| Tambah | `form` | Form tugas baru |
| Detail | `detail/{id}` | Info lengkap, ubah status, tombol edit/hapus |
| Edit | `form?id={id}` | Form yang sama, terisi data lama |

Layar tambah dan edit memakai satu composable dan satu ViewModel; perbedaannya hanya ada tidaknya argumen `id`. Ini mengurangi duplikasi kode.

### Model data (tabel `tasks`)
| Field | Tipe | Keterangan |
|---|---|---|
| id | Long (PK, auto) | Pembeda tiap tugas |
| mataKuliah | String | |
| judul | String | |
| deskripsi | String | |
| deadline | String `yyyy-MM-dd` | Untuk pengurutan dan penanda penting |
| prioritas | String | Rendah / Sedang / Tinggi |
| status | String | Belum / Proses / Selesai; dasar filter dan progres |

### Penyimpanan lokal
Room (SQLite) di perangkat. Tidak ada permintaan jaringan sama sekali, sehingga offline-first secara alami.

### Arsitektur
Compose UI → ViewModel → Repository → Room DAO → Database; hasil data mengalir kembali sebagai UI State.

## 3. Alasan Teknis
| Keputusan | Alasan |
|---|---|
| Room, bukan variabel/`SharedPreferences` | Data terstruktur, perlu query (urut, filter), dan harus bertahan saat proses aplikasi dimatikan |
| DAO mengembalikan `Flow` | Daftar otomatis diperbarui setiap data berubah; tidak perlu refresh manual |
| ViewModel + `StateFlow` | State bertahan saat rotasi layar dan terpisah dari UI sehingga mudah diuji |
| Repository di antara ViewModel dan DAO | ViewModel tidak bergantung pada sumber data; sumber lain (mis. API) bisa ditambah tanpa mengubah UI |
| UI tidak mengakses database | Pemisahan tanggung jawab: UI hanya menampilkan state dan mengirim event |
| Deadline disimpan sebagai teks ISO | Urutan alfabet sama dengan urutan tanggal, tanpa `TypeConverter` tambahan |
| Status/prioritas sebagai String | Sederhana; nama status boleh disesuaikan (sesuai catatan slide) |
| Satu composable untuk tambah & edit | Menghindari duplikasi dan menjaga perilaku form konsisten |
| `@Upsert` | Satu fungsi untuk insert (id=0) dan update |
| Dialog konfirmasi hapus | Mencegah kehilangan data karena salah sentuh |
| Dependency injection manual (`TugasApp` + factory) | Cukup untuk skala kecil; tidak menambah library (Hilt) yang belum perlu |

## 4. Keterkaitan Materi
M1 Kotlin (data class, object, coroutines) · M2 Git (riwayat commit per milestone) · M3 Compose UI (screen, chip, card) · M4 State & Event (`StateFlow`, `mutableStateOf`, callback event) · M5 Navigation & ViewModel · M6 Room (Entity, DAO, Database).
