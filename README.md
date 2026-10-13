# TugasKu – Aplikasi Manajemen Tugas Mahasiswa (Offline)

Praktikum 1 – Pemrograman Aplikasi Bergerak, Universitas Mulia.
Stack: Kotlin, Jetpack Compose, ViewModel, Navigation Compose, Room.

## Deskripsi Aplikasi
Mahasiswa sering menerima tugas dari beberapa mata kuliah, dan informasinya tersebar di chat, LMS, atau catatan pribadi.
TugasKu membantu mahasiswa **mencatat, memantau, dan memperbarui status tugas** meskipun sedang offline.
Semua data disimpan lokal di perangkat memakai Room, sehingga aplikasi tidak memerlukan internet
dan data tidak hilang saat aplikasi ditutup.

## Fitur
**Wajib**
1. Menampilkan daftar tugas dari database lokal (diurutkan menurut deadline; tugas selesai di bawah)
2. Menambah tugas baru melalui form input (dengan validasi)
3. Melihat detail satu tugas secara lengkap
4. Mengubah data tugas yang sudah tersimpan
5. Menghapus tugas dengan konfirmasi pengguna
6. Mengubah status: Belum, Proses, atau Selesai

**Opsional**
- Filter berdasarkan status
- Pencarian berdasarkan judul atau mata kuliah
- Indikator tenggat dekat (sisa ≤ 2 hari) atau terlambat

## Cara Menjalankan
**Prasyarat:** Android Studio (Ladybug atau lebih baru), JDK 17, emulator atau perangkat Android 8.0+ (API 26).
1. Clone repository: `git clone <URL_REPO>` lalu buka folder `TugasKu` di Android Studio
   (atau ekstrak zip dan buka foldernya).
2. Tunggu Gradle Sync selesai (sync pertama butuh internet untuk mengunduh dependensi; setelah itu aplikasi tidak butuh internet).
3. Pilih emulator/perangkat, lalu tekan **Run ▶** (`app`).
4. Cara memakai: tekan **+** untuk menambah tugas, ketuk kartu tugas untuk melihat detail, lalu ubah status, edit, atau hapus dari layar detail.

## Arsitektur
Compose UI → ViewModel → Repository → Room DAO → Database
(hasil data → UI State → tampilan berubah)

| Lapisan | File |
|---|---|
| Screen | `ui/Screens.kt` (Daftar, Detail, Form tambah/edit) |
| Navigasi | `ui/AppNav.kt` |
| State + ViewModel | `ui/ViewModels.kt` |
| Data layer | `data/` (Task, TaskDao, AppDatabase, TaskRepository) |

UI tidak pernah menyentuh database secara langsung; semua lewat ViewModel → Repository.

## Uji offline & persistensi (untuk demo)
1. Aktifkan mode pesawat → aplikasi tetap berjalan (tidak ada akses internet sama sekali).
2. Tambah beberapa tugas → tutup aplikasi (swipe dari recent apps) → buka lagi → data masih ada.
3. Tunjukkan edit, ubah status, filter, cari, dan hapus.

## Git
Repositori ini sudah berisi riwayat commit per tahap (`git log --oneline`).
Sebelum push, ganti identitas penulis dengan nama Anda:
```bash
git config user.name "Nama Anda"
git config user.email "email@anda"
git remote add origin <URL_REPO>
git push -u origin main
```
Riwayat memakai nama placeholder; jika ingin author commit memakai nama Anda, jalankan
`git rebase -r --root --exec 'git commit --amend --reset-author --no-edit'` setelah mengatur identitas di atas.
Lanjutkan dengan commit kecil dan bermakna (contoh: `fix(ui): ...`, `feat(data): ...`).

## Dokumen
`docs/RANCANGAN.md` berisi analisis kebutuhan, rancangan alur/navigasi/model data, dan alasan teknis.
