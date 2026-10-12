package sem3matkul.core;

import java.sql.SQLException;
import java.sql.Connection;
import sem3matkul.core.CliHelper.*;
import sem3matkul.pertemuan4.cli.*;
import sem3matkul.pertemuan5.*;

/**
 * CliManager - dispatcher modular untuk mode CLI.
 * Dipanggil dari App.java ketika user memilih choice 1 (CLI).
 * Kedepan tinggal tambah case untuk pertemuan lain:
 *   switch(pertemuan) { case 2 -> sem3matkul.pertemuan2.cli.ThreadCli.run(); ... }
 */
public class CliManager {

    /**
     * Entry point CLI. Untuk sekarang langsung jalankan Pertemuan 2 ThreadCli.
     * Nanti bisa dikembangkan jadi menu pilih pertemuan modular.
     */
    public static void start() {
        Log.println("\n[ CliManager ] Mode CLI aktif");
        Log.println("Pilih Pertemuan berapa:");
        Log.println("2. Thread");
        Log.println("4. Api");
        Log.println("5. Api Sederhana");
        Log.println("6. Koneksi MYSQL Sederhana");
        Log.print("Pilihan mu: ");
        int pertemuan = CliHelper.inputInt();
        switch (pertemuan) {
            case 2:
                Log.println("[ CliManager ] Menjalankan Pertemuan 2 - Thread");
                sem3matkul.pertemuan2.cli.ThreadCli.run();
                break;
            case 4:
                Log.println("[ CliManager ] Menjalankan Pertemuan 4 - API");
                ChatBotCLI.run();
                break;
            case 5:
                Log.println("[ CliManager ] Menjalankan Pertemuan 5 - API Sederhana");
                ApiSederhana sederhana = new ApiSederhana();
                sederhana.main();
                break;
            case 6:
                Log.println("[ CliManager ] Menjalankan Pertemuan 6 - Koneksi & Skema Database Perpustakaan");
                Log.println("Pilih Aksi Database:");
                Log.println("1. Test Koneksi");
                Log.println("2. Inisialisasi Database (Create table jika belum ada)");
                Log.println("3. Rescheme Database (Drop & buat ulang + seed dummy data)");
                Log.println("4. Cek Status & Jumlah Data Tabel");
                Log.print("Pilihan aksi: ");
                int dbAksi = CliHelper.inputInt();
                try {
                    switch (dbAksi) {
                        case 1:
                            try (Connection conn = Database.connect()) {
                                Log.println("✅ Berhasil terhubung ke database: " + Database.getUrl());
                            }
                            break;
                        case 2:
                            DbInitializer.initDatabase();
                            DbInitializer.printTableStatus();
                            break;
                        case 3:
                            DbInitializer.rescheme(true);
                            DbInitializer.printTableStatus();
                            break;
                        case 4:
                            DbInitializer.printTableStatus();
                            break;
                        default:
                            Log.println("Pilihan tidak valid.");
                            break;
                    }
                } catch (SQLException e) {
                    System.err.println("❌ Kesalahan Database: " + e.getMessage());
                }
                break;
            default:
                Log.println("[ CliManager ] Pertemuan " + pertemuan + " belum tersedia di CLI");
                // fallback ke default
                sem3matkul.pertemuan2.cli.ThreadCli.run();
                break;
        }
        // Modular call - pertemuan 2 (buat scanner baru jika dipanggil tanpa scanner)
        System.exit(0);
    }
}
