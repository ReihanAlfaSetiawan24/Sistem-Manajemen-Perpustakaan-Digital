import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.io.*;


// Kelas representasi dari entitas Buku
class Buku {
    int id;
    String nama;
    String kategori;
    String penulis;
    int tahunTerbit;
    String status; // Menyimpan status buku: available (tersedia), borrowed (dipinjam), deleted (dihapus sementara)

    // Konstruktor untuk inisialisasi objek Buku baru
    Buku(int id, String nama, String kategori, String penulis, int tahunTerbit, String status) {
        this.id = id;
        this.nama = nama;
        this.kategori = kategori;
        this.penulis = penulis;
        this.tahunTerbit = tahunTerbit;
        this.status = status;
    }

    // Method overriding untuk mengembalikan format string yang sesuai untuk disimpan ke file CSV
    @Override
    public String toString() {
        // tetap simpan kolom CSV sesuai existing format: jumlah_stok diisi 0
        return id + "," + nama + "," + kategori + "," + penulis + "," + tahunTerbit + ",0," + status;
    }
}


public class PerpusDigital {
    // List memori sementara untuk menyimpan semua objek buku saat program berjalan
    private static List<Buku> bukuList = new ArrayList<>();
    // Variabel penanda untuk ID otomatis selanjutnya
    private static int nextId = 1;

    public static void main(String[] args) {
        // Memuat data dari file CSV ke dalam memori (bukuList) saat program pertama kali dijalankan
        loadData();
        Scanner sc = new Scanner(System.in);
        int pilihan;
        
        // Looping utama untuk menampilkan Menu Interaktif CLI
        do {
            System.out.println("\n=== SISTEM MANAJEMEN PERPUSTAKAAN DIGITAL ===");
            System.out.println("1. Tambah buku");
            System.out.println("2. Tampil semua");
            System.out.println("3. Edit buku");
            System.out.println("4. Hapus (soft)");
            System.out.println("5. Search (id/nama/kategori)");
            System.out.println("6. Urutkan buku");
            System.out.println("7. Update status");
            System.out.println("8. Statistik");
            System.out.println("9. Simpan");
            System.out.println("0. Keluar");
            System.out.print("Pilih: ");
            pilihan = sc.nextInt();
            sc.nextLine(); // Membersihkan buffer newline

            // Navigasi menu berdasarkan pilihan user menggunakan percabangan Switch-Case
            switch (pilihan) {
                case 1 -> tambahBuku(sc);
                case 2 -> tampilSemua();
                case 3 -> editBuku(sc);
                case 4 -> hapusBuku(sc);
                case 5 -> searchUnifikasi(sc);
                case 6 -> urutkanBuku(sc);
                case 7 -> updateStatus(sc);
                case 8 -> tampilStatistik();
                case 9 -> simpanData();
                case 0 -> simpanData(); // Otomatis menyimpan data sebelum keluar program
                default -> System.out.println("Invalid!");
            }
        } while (pilihan != 0); // Program berhenti jika user memilih 0
        sc.close();
    }

    // Fungsi untuk menambahkan entri buku baru
    static void tambahBuku(Scanner sc) {
        int id;
        // Looping validasi ID buku
        while (true) {
            System.out.print("ID buku: ");
            id = sc.nextInt();
            sc.nextLine();

            // Sesuai permintaan:
            // 1) Jika ID sudah terpakai (status != deleted) => minta user input ID lain.
            // 2) Jika ID lebih kecil dari id terbesar => geser ke nextId TAPI hanya jika ID tersebut tidak terpakai.
            if (idTerpakai(id)) {
                System.out.println("ID telah terpakai, silakan coba dengan id lain");
                continue; // Mengulang proses input ID
            }

            // Menangani kasus jika ID valid tapi lebih kecil dari ID maksimal dalam sistem
            if (idLebihKecilDariIdTersimpanTerbesar(id)) {
                int candidate = nextId;
                if (idTerpakai(candidate)) {
                    System.out.println("ID telah terpakai, silakan coba dengan id lain");
                    continue;
                }
                id = candidate; // Mengganti ID yang kecil tadi dengan ID terbesar yang tersedia (auto-increment)
                System.out.println("ID yang dimasukkan lebih kecil dari id terbesar. Diganti menjadi: " + id);
            }

            break; // Keluar dari loop validasi jika ID sudah benar-benar aman
        }

        // Meminta input data detail buku
        System.out.print("Nama buku: ");
        String nama = sc.nextLine();
        System.out.print("Kategori: ");
        String kat = sc.nextLine();
        System.out.print("Penulis: ");
        String penulis = sc.nextLine();
        System.out.print("Tahun terbit: ");
        int tahun = sc.nextInt();
        sc.nextLine();

        // Memperbarui nextId agar selalu lebih besar dari ID terakhir yang dimasukkan
        if (id >= nextId) nextId = id + 1;
        
        // Memasukkan objek buku baru ke dalam list dengan status default 'available'
        bukuList.add(new Buku(id, nama, kat, penulis, tahun, "available"));
        System.out.println("Buku ID " + id + " ditambah!");
    }



    // Fungsi untuk menampilkan daftar seluruh buku di perpustakaan
    static void tampilSemua() {

        if (bukuList.isEmpty()) {


            System.out.println("Tidak ada buku.");
            return; // Menghentikan fungsi jika list kosong
        }
        // Format Header tabel
        System.out.printf("%-5s %-32s %-15s %-20s %-8s %s%n", "ID", "Nama", "Kategori", "Penulis", "Tahun", "Status");
        System.out.println(new String(new char[100]).replace("\0", "-")); // Membuat garis pembatas
        
            // (acak dilakukan saat load agar hasil tampil terlihat acak)
            // Melakukan iterasi semua buku di list

            for (Buku b : bukuList) {

            // Hanya menampilkan buku yang statusnya TIDAK dihapus (deleted)
            if (!b.status.equals("deleted")) {
                System.out.printf("%-5d %-32s %-15s %-20s %-8d %s%n", b.id, b.nama, b.kategori, b.penulis, b.tahunTerbit, b.status);
            }
        }

    }

    // Fungsi untuk mengubah detail informasi dari buku yang sudah ada
    static void editBuku(Scanner sc) {
        System.out.print("ID edit: ");
        int id = sc.nextInt();
        sc.nextLine();
        
        // Pencarian buku (Linear Search) berdasarkan ID yang diinputkan
        for (Buku b : bukuList) {
            if (b.id == id && !b.status.equals("deleted")) {
                // Meniban data lama dengan inputan data baru
                System.out.print("Nama baru: ");
                b.nama = sc.nextLine();
                System.out.print("Kategori baru: ");
                b.kategori = sc.nextLine();
                System.out.print("Penulis baru: ");
                b.penulis = sc.nextLine();
                System.out.print("Tahun baru: ");
                b.tahunTerbit = sc.nextInt();
                sc.nextLine();

                System.out.println("Diedit!");
                return; // Menghentikan pencarian dan fungsi setelah berhasil edit
            }
        }
        System.out.println("Tidak ditemukan."); // Pesan error jika ID tidak valid
    }

    // Fungsi untuk menghapus buku dengan metode "Soft Delete" (data tidak hilang dari sistem, hanya ganti status)
    static void hapusBuku(Scanner sc) {
        System.out.print("ID hapus: ");
        int id = sc.nextInt();
        sc.nextLine();
        
        // Mencari buku di database in-memory
        for (Buku b : bukuList) {
            if (b.id == id && !b.status.equals("deleted")) {
                // Ubah status bukunya saja, data masih tersimpan untuk riwayat/statistik
                b.status = "deleted";
                System.out.println("Dihapus soft.");
                return;
            }
        }
        System.out.println("Tidak ditemukan.");
    }

    // Fungsi satu gerbang untuk pencarian buku dengan tiga kriteria berbeda
    static void searchUnifikasi(Scanner sc) {
        System.out.print("Cari apa? (id/nama/kategori): ");
        String tipe = sc.nextLine().toLowerCase();
        System.out.print("Keyword/ID: ");
        String keyword = sc.nextLine();

        boolean found = false; // Flag status pencarian
        
        // Kriteria 1: Pencarian berdasarkan ID
        if (tipe.equals("id")) {
            try {
                int idCari = Integer.parseInt(keyword);
                List<Buku> temp = new ArrayList<>();
                // Menyaring buku yang tidak terhapus ke dalam list temporary
                for (Buku b : bukuList) {
                    if (!b.status.equals("deleted")) temp.add(b);
                }
                // Melakukan Bubble Sort untuk mengurutkan ID di list temporary sebagai syarat Binary Search
                for (int i = 0; i < temp.size() - 1; i++) {
                    for (int j = 0; j < temp.size() - i - 1; j++) {
                        if (temp.get(j).id > temp.get(j + 1).id) {
                            Buku swap = temp.get(j);
                            temp.set(j, temp.get(j + 1));
                            temp.set(j + 1, swap);
                        }
                    }
                }
                // Algoritma Binary Search untuk mencari buku secara efisien (membagi 2 rentang pencarian)
                int low = 0, high = temp.size() - 1;
                while (low <= high) {
                    int mid = low + (high - low) / 2;
                    if (temp.get(mid).id == idCari) { // Jika ditemukan di tengah
                        Buku b = temp.get(mid);
                        System.out.printf("ID %d: %s by %s (%s, %d)%n", idCari, b.nama, b.penulis, b.kategori, b.tahunTerbit);

                        found = true;
                        break;
                    } else if (temp.get(mid).id < idCari) low = mid + 1; // Geser batas pencarian bawah
                    else high = mid - 1; // Geser batas pencarian atas
                }
            } catch (NumberFormatException e) {
                System.out.println("ID angka!"); // Handling error jika input bukan tipe integer
            }
        
        // Kriteria 2: Pencarian berdasarkan Nama (Linear Search / String matching)
        } else if (tipe.equals("nama")) {
            for (Buku b : bukuList) {
                // Ignore case (toLowerCase) dan mencocokkan kemiripan string pakai '.contains'
                if (!b.status.equals("deleted") && b.nama.toLowerCase().contains(keyword.toLowerCase())) {
                    System.out.printf("ID %d: %s by %s (%s, %d)%n", b.id, b.nama, b.penulis, b.kategori, b.tahunTerbit);

                    found = true;
                }
            }
        
        // Kriteria 3: Pencarian berdasarkan Kategori
        } else if (tipe.equals("kategori")) {
            System.out.printf("Kategori '%s':%n", keyword);
            System.out.printf("%-5s %-32s %-15s %-20s %-8s %s%n", "ID", "Nama", "Kategori", "Penulis", "Tahun", "Status");

            System.out.println(new String(new char[85]).replace("\0", "-"));
            for (Buku b : bukuList) {
                // Sama halnya pencarian nama, mencocokkan string kategori (ignore case)
                if (!b.status.equals("deleted") && b.kategori.toLowerCase().contains(keyword.toLowerCase())) {
                    System.out.printf("%-5d %-32s %-15s %-20s %-8d %s%n", b.id, b.nama, b.kategori, b.penulis, b.tahunTerbit, b.status);
                    found = true;

                }
            }
        }
        // Eksekusi jika flag 'found' masih false (buku tidak ditemukan sama sekali)
        if (!found) System.out.println("Tidak ditemukan.");
    }

    // Fungsi sub-menu untuk mengurutkan (Sorting) isi perpustakaan
    static void urutkanBuku(Scanner sc) {
        System.out.println("1. Urutkan berdasarkan ID (bubble asc)");
        System.out.println("2. Urutkan berdasarkan nama (selection asc)");
        System.out.println("3. Urutkan berdasarkan kategori (ascending A-Z)");


        System.out.print("Pilih: ");
        int subPilihan = sc.nextInt();
        sc.nextLine();
        switch (subPilihan) {
            case 1 -> {
                sortIdAsc(); // Pemanggilan method pengurutan ID
                tampilSemua(); // Menampilkan data setelah diurutkan
            }
            case 2 -> {
                sortNamaAsc(); // Pemanggilan method pengurutan nama
                tampilSemua(); 
            }
            case 3 -> {
                sortKategoriAsc();
                tampilSemua();
            }



            default -> System.out.println("Invalid!");
        }
    }

    // Algoritma pengurutan Bubble Sort secara Ascending (terkecil ke terbesar) berdasarkan integer ID
    static void sortIdAsc() {
        for (int i = 0; i < bukuList.size() - 1; i++) {
            for (int j = 0; j < bukuList.size() - i - 1; j++) {
                // Pengecekan status agar indeks buku yang dihapus tidak ikut dibandingkan
                // Melakukan pertukaran (swap) jika objek sebelahnya lebih kecil angkanya
                if (!bukuList.get(j).status.equals("deleted") && !bukuList.get(j + 1).status.equals("deleted") &&
                    bukuList.get(j).id > bukuList.get(j + 1).id) {
                    Buku swap = bukuList.get(j);
                    bukuList.set(j, bukuList.get(j + 1));
                    bukuList.set(j + 1, swap);
                }
            }
        }
        System.out.println("Urut ID asc (bubble).");
    }

    // Algoritma pengurutan Selection Sort secara Ascending berdasarkan String nama buku
    static void sortNamaAsc() {
        for (int i = 0; i < bukuList.size() - 1; i++) {
            int minIdx = i; // Menganggap elemen index 'i' adalah abjad terkecil (A paling kecil)
            for (int j = i + 1; j < bukuList.size(); j++) {
                // CompareToIgnoreCase akan menghasilkan angka negatif jika string kiri lebih kecil (berdasarkan urutan Lexicographic)
                if (!bukuList.get(j).status.equals("deleted") && !bukuList.get(minIdx).status.equals("deleted") &&
                    bukuList.get(j).nama.compareToIgnoreCase(bukuList.get(minIdx).nama) < 0) {
                    minIdx = j; // Update index buku dengan abjad nama terkecil
                }
            }
            // Jika ada nama dengan awalan abjad yang lebih kecil, maka elemen index 'i' di swap dengan elemen terkecil tsb.
            if (minIdx != i) {
                Buku swap = bukuList.get(i);
                bukuList.set(i, bukuList.get(minIdx));
                bukuList.set(minIdx, swap);
            }
        }
        System.out.println("Urut nama asc (selection).");
    }

    static void sortKategoriAsc() {
        for (int i = 0; i < bukuList.size() - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < bukuList.size(); j++) {
                if (!bukuList.get(j).status.equals("deleted") &&
                    !bukuList.get(minIdx).status.equals("deleted") &&
                    bukuList.get(j).kategori.compareToIgnoreCase(bukuList.get(minIdx).kategori) < 0) {
                    minIdx = j;
                }
            }
            if (minIdx != i) {
                Buku swap = bukuList.get(i);
                bukuList.set(i, bukuList.get(minIdx));
                bukuList.set(minIdx, swap);
            }
        }
        System.out.println("Urut kategori asc (selection).");
    }


    // Fungsi transaksional untuk meminjam/mengembalikan buku dengan mengubah atribut status

    static void updateStatus(Scanner sc) {
        System.out.print("ID: ");
        int id = sc.nextInt();
        sc.nextLine();
        for (Buku b : bukuList) {
            if (b.id == id && !b.status.equals("deleted")) {
                System.out.println("1. available 2. borrowed");
                int p = sc.nextInt();
                sc.nextLine();
                // Menggunakan Ternary Operator. Jika p==1 maka available, selain itu borrowed.
                b.status = (p == 1) ? "available" : "borrowed";
                System.out.println("Status update!");
                return;
            }
        }
        System.out.println("Tidak ditemukan.");
    }

    // Fungsi Laporan (Report) menghitung kuantitas buku berdasarkan kategorinya
    static void tampilStatistik() {
        int total = 0, available = 0, borrowed = 0;
        for (Buku b : bukuList) {
            if (!b.status.equals("deleted")) {
                total++; // Increments Total buku yang tidak dihapus
                if (b.status.equals("available")) available++; // Hitung yang statusnya tersedia
                else if (b.status.equals("borrowed")) borrowed++; // Hitung yang sedang dipinjam
            }
        }
        System.out.println("=== Statistik ===");
        System.out.println("Total buku: " + total);
        System.out.println("Tersedia: " + available);
        System.out.println("Dipinjam: " + borrowed);
        // Buku deleted dihitung dari selisih jumlah record memori asli dikurangi dengan buku yang masih valid
        System.out.println("Deleted: " + (bukuList.size() - total));
    }

    // Fungsi File I/O untuk mem-backup isi memori 'bukuList' ke file bentuk teks (CSV)
    static void simpanData() {
        // Menggunakan 'try-with-resources' yang akan otomatis menutup (close) PrintWriter selesai dieksekusi
        try (PrintWriter pw = new PrintWriter(new FileWriter("data_buku.csv"))) {
            // Tulis Header Kolom
            pw.println("id,nama,kategori,penulis,tahun_terbit,jumlah_stok,status");
            // Tulis ulang tiap row buku (Memanggil fungsi method overriding .toString() milik kelas Buku)
            for (Buku b : bukuList) {
                pw.println(b.toString());
            }
            System.out.println("Disimpan ke data_buku.csv");
        } catch (IOException e) {
            System.out.println("Gagal simpan!");
        }
    }

    // Method pembantu (helper) mengecek duplikasi ID
    static boolean idTerpakai(int id) {
        for (Buku b : bukuList) {
            if (b.id == id && !b.status.equals("deleted")) return true; // Mengembalikan true jika ID sudah ada
        }
        return false;
    }

    // jika user memasukkan id lebih kecil dari id terbesar yang sudah ada di sistem, maka id digeser ke atas
    static boolean idLebihKecilDariIdTersimpanTerbesar(int inputId) {
        // nextId = maxId+1 berdasarkan loadData/tambahBuku
        int maxId = nextId - 1; // Mendapatkan ID tertinggi yang ada di database saat ini
        return inputId < maxId;
    }

    // Fungsi File I/O untuk mengurai (parsing) teks CSV kembali menjadi objek Buku ke memori 'bukuList'
    static void loadData() {
        // try-with-resources membaca file csv stream per baris text
        try (BufferedReader br = new BufferedReader(new FileReader("data_buku.csv"))) {
            br.readLine(); // header (Membuang pembacaan baris pertama CSV / Header judul kolom)
            String line;
            // Looping terus selama baris belum null (habis)
            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue; // Skip jika barisnya kosong
                String[] parts = line.split(","); // Memecah string dengan delimiter koma (,)
                // id,nama,kategori,penulis,tahun_terbit,jumlah_stok,status
                if (parts.length < 7) { // Validasi memastikan baris memiliki 7 kolom yang sesuai format
                    throw new IOException("Format CSV tidak valid: " + line);
                }
                // jumlah_stok diabaikan (tetap dianggap ada 0 stok)
                // Membentuk objek buku kembali menggunakan nilai array (indeks parsing Integer untuk angka)
                Buku b = new Buku(Integer.parseInt(parts[0].trim()), parts[1], parts[2], parts[3], Integer.parseInt(parts[4].trim()), parts[6]);
                bukuList.add(b); // Append hasil load ke list

                // Adaptasi agar sistem otomatis mengingat ID terbesar untuk nextId (Auto-Increment)
                if (b.id >= nextId) nextId = b.id + 1;
            }

            // Acak urutan data setelah load (sekali)
            Collections.shuffle(bukuList);

            System.out.println(bukuList.size() + " data loaded.");



        } catch (FileNotFoundException e) {
            System.out.println("File baru."); // Jika file CSV tidak ditemukan akan membuat baru nantinya
        } catch (IOException | NumberFormatException e) {
            System.out.println("Load error: " + e.getMessage()); // Menangkap masalah I/O reading atau corrupt data parsing
        }
    }
}