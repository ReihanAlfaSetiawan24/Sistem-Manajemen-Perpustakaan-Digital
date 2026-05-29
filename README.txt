================================================
README.txt
SISTEM PERPUSTAKAAN DIGITAL
===========================

# DESKRIPSI PROGRAM

---

Program ini merupakan aplikasi Sistem
Manajemen Perpustakaan Digital berbasis
Java menggunakan Command Line Interface (CLI).

Fitur program:

* Tambah buku
* Tampil semua buku
* Edit buku
* Hapus buku (soft delete)
* Search buku
* Sorting buku
* Update status buku
* Statistik perpustakaan
* Simpan data CSV

================================================

# PERSYARATAN

---

* Java Development Kit (JDK) versi 8+
* Command Prompt / Terminal
* IDE (Opsional)
  • IntelliJ IDEA
  • Eclipse
  • VS Code

================================================

# CARA MENJALANKAN PROGRAM

---

1. Buka folder project

2. Compile program:

   javac PerpusDigital.java

3. Jalankan program:

   java PerpusDigital

4. Program akan menampilkan menu utama

================================================

# MENU PROGRAM

---

1. Tambah buku
2. Tampil semua
3. Edit buku
4. Hapus (soft)
5. Search (id/nama/kategori)
6. Urutkan buku
7. Update status
8. Statistik
9. Simpan
10. Keluar

================================================

# PENYIMPANAN DATA

---

Program menggunakan file CSV:

data_buku.csv

File akan:

* Dibuat otomatis saat simpan data
* Dibaca otomatis saat program dijalankan

================================================

# STRUKTUR FILE

---

PerpusDigital.java   → Source code utama
data_buku.csv        → Database penyimpanan
README.txt           → Panduan menjalankan program

================================================

# ALGORITMA YANG DIGUNAKAN

---

* Binary Search
* Linear Search
* Bubble Sort
* Selection Sort

================================================

# CATATAN

---

* Sistem menggunakan ArrayList
  untuk penyimpanan data sementara

* Data dengan status "deleted"
  tidak akan tampil di menu utama

* Program menggunakan File I/O
  untuk membaca dan menyimpan data

================================================
