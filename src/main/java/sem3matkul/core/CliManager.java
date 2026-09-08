package sem3matkul.core;

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
        System.out.println("\n[ CliManager ] Mode CLI aktif");
        // Modular call - pertemuan 2 (buat scanner baru jika dipanggil tanpa scanner)
        sem3matkul.pertemuan2.cli.ThreadCli.run();
        System.exit(0);
    }

    public static void start(java.util.Scanner scanner) {
        System.out.println("\n[ CliManager ] Mode CLI aktif");
        sem3matkul.pertemuan2.cli.ThreadCli.run(scanner);
        System.exit(0);
    }

    /**
     * Overload untuk dispatch spesifik pertemuan (untuk pengembangan modular).
     * @param pertemuan nomor pertemuan, mis. 2 untuk Thread
     */
    public static void start(int pertemuan) {
        switch (pertemuan) {
            case 2:
                System.out.println("[ CliManager ] Menjalankan Pertemuan 2 - Thread");
                sem3matkul.pertemuan2.cli.ThreadCli.run();
                break;
            default:
                System.out.println("[ CliManager ] Pertemuan " + pertemuan + " belum tersedia di CLI");
                // fallback ke default
                sem3matkul.pertemuan2.cli.ThreadCli.run();
                break;
        }
    }
}
