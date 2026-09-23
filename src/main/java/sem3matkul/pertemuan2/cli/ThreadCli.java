package sem3matkul.pertemuan2.cli;

/**
 * Demo PBO Thread Pertemuan 2 - Versi CLI
 * Di-run via CliManager ketika user memilih mode CLI (choice 1).
 * Menggunakan Thread & Runnable sesuai materi.
 */
import sem3matkul.core.CliHelper.*;
import sem3matkul.core.CliHelper;

public class ThreadCli {

    /** Entry modular yang dipanggil CliManager - loop dengan prompt ulang */
    public static void run() {
        run(CliHelper.getScanner());
    }

    public static void run(java.util.Scanner scanner) {
        // Loop utama - reset dari 0 tiap iterasi
        while (true) {
            Log.println("\n=== PERTEMUAN 2: Thread (CLI Mode) ===");
            new Kasir();

            Log.println("\nIngin Mengulanginya lagi?");
            Log.println("1. Ya");
            Log.println("2. No");
            Log.print("Pilih: ");

            int choice = CliHelper.inputInt();

            switch (choice) {
                case 1:
                    Log.println("\n--- Reset dari 0 ---\n");
                    // lanjut loop, akan buat Kasir baru (reset dari 0)
                    break;
                case 2:
                    Log.println("Selesai. Keluar CLI.");
                    return;
                default:
                    Log.println("Invalid Choice - keluar.");
                    return;
            }
        }
    }

    // --- Kasir 1: melayani pelanggan 6-10 di thread terpisah ---
    static class Kasir1 implements Runnable {
        @Override
        public void run() {
            String newThread = java.lang.Thread.currentThread().getName();
            Log.println("New Thread: " + newThread);

            for (int pel = 6; pel <= 10; pel++) {
                Log.println(newThread + " Sedang melayani " + pel);
                try {
                    java.lang.Thread.sleep(400);
                } catch (InterruptedException e) {
                    java.lang.Thread.currentThread().interrupt();
                    Log.println(newThread + " terinterupsi");
                    break;
                }
            }
        }
    }

    // --- Kasir 2: melayani pelanggan 11-15 di thread terpisah ---
    static class Kasir2 implements Runnable {
        @Override
        public void run() {
            String newThread = java.lang.Thread.currentThread().getName();
            Log.println("New Thread: " + newThread);

            for (int pel = 11; pel <= 15; pel++) {
                Log.println(newThread + " Sedang melayani " + pel);
                try {
                    java.lang.Thread.sleep(400);
                } catch (InterruptedException e) {
                    java.lang.Thread.currentThread().interrupt();
                    Log.println(newThread + " terinterupsi");
                    break;
                }
            }
        }
    }

    // --- Kasir Utama: melayani 1-5 di thread utama + spawn 2 thread kasir ---
    static class Kasir {
        Kasir() {
            String thisThread = java.lang.Thread.currentThread().getName();
            Log.println("Thread Utama: " + thisThread);

            java.lang.Thread newKasir1 = new java.lang.Thread(new Kasir1(), "Kasir-1");
            newKasir1.start();

            java.lang.Thread newKasir2 = new java.lang.Thread(new Kasir2(), "Kasir-2");
            newKasir2.start();

            for (int pel = 1; pel <= 5; pel++) {
                Log.println(thisThread + " Sedang melayani " + pel);
                try {
                    java.lang.Thread.sleep(1000);
                } catch (InterruptedException e) {
                    java.lang.Thread.currentThread().interrupt();
                    Log.println(thisThread + " terinterupsi");
                    break;
                }
            }

            // Tunggu kedua kasir selesai agar output CLI rapi (join)
            try {
                newKasir1.join();
                newKasir2.join();
            } catch (InterruptedException e) {
                java.lang.Thread.currentThread().interrupt();
            }

            Log.println("=== Semua pelanggan selesai dilayani (15) ===");
        }
    }
}
