package sem3matkul.pertemuan2.cli;

/**
 * Demo PBO Thread Pertemuan 2 - Versi CLI
 * Di-run via CliManager ketika user memilih mode CLI (choice 1).
 * Menggunakan Thread & Runnable sesuai materi.
 */
import sem3matkul.core.CliHelper;

public class ThreadCli {

    /** Entry modular yang dipanggil CliManager - loop dengan prompt ulang */
    public static void run() {
        run(new java.util.Scanner(System.in));
    }

    public static void run(java.util.Scanner scanner) {
        // Loop utama - reset dari 0 tiap iterasi
        while (true) {
            CliHelper.print("\n=== PERTEMUAN 2: Thread (CLI Mode) ===");
            new Kasir();

            CliHelper.print("\nIngin Mengulanginya lagi?");
            CliHelper.print("1. Ya");
            CliHelper.print("2. No");
            System.out.print("Pilih: ");
            System.out.flush();

            int choice = CliHelper.inputInt();

            switch (choice) {
                case 1:
                    System.out.println("\n--- Reset dari 0 ---\n");
                    // lanjut loop, akan buat Kasir baru (reset dari 0)
                    break;
                case 2:
                    System.out.println("Selesai. Keluar CLI.");
                    return;
                default:
                    System.out.println("Invalid Choice - keluar.");
                    return;
            }
        }
    }

    // --- Kasir 1: melayani pelanggan 6-10 di thread terpisah ---
    static class Kasir1 implements Runnable {
        @Override
        public void run() {
            String newThread = java.lang.Thread.currentThread().getName();
            System.out.println("New Thread: " + newThread);

            for (int pel = 6; pel <= 10; pel++) {
                System.out.println(newThread + " Sedang melayani " + pel);
                try {
                    java.lang.Thread.sleep(400);
                } catch (InterruptedException e) {
                    java.lang.Thread.currentThread().interrupt();
                    System.out.println(newThread + " terinterupsi");
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
            System.out.println("New Thread: " + newThread);

            for (int pel = 11; pel <= 15; pel++) {
                System.out.println(newThread + " Sedang melayani " + pel);
                try {
                    java.lang.Thread.sleep(400);
                } catch (InterruptedException e) {
                    java.lang.Thread.currentThread().interrupt();
                    System.out.println(newThread + " terinterupsi");
                    break;
                }
            }
        }
    }

    // --- Kasir Utama: melayani 1-5 di thread utama + spawn 2 thread kasir ---
    static class Kasir {
        Kasir() {
            String thisThread = java.lang.Thread.currentThread().getName();
            System.out.println("Thread Utama: " + thisThread);

            java.lang.Thread newKasir1 = new java.lang.Thread(new Kasir1(), "Kasir-1");
            newKasir1.start();

            java.lang.Thread newKasir2 = new java.lang.Thread(new Kasir2(), "Kasir-2");
            newKasir2.start();

            for (int pel = 1; pel <= 5; pel++) {
                System.out.println(thisThread + " Sedang melayani " + pel);
                try {
                    java.lang.Thread.sleep(1000);
                } catch (InterruptedException e) {
                    java.lang.Thread.currentThread().interrupt();
                    System.out.println(thisThread + " terinterupsi");
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

            System.out.println("=== Semua pelanggan selesai dilayani (15) ===");
        }
    }
}
