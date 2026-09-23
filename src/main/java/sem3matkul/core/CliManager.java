package sem3matkul.core;

import sem3matkul.core.CliHelper.*;
import sem3matkul.pertemuan4.cli.ChatBotCLI;

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
