package sem3matkul;

import sem3matkul.core.CliManager;

/**
 * Entry point khusus mode CLI/TUI.
 *
 * Dipakai oleh jalankan-cli.sh supaya aplikasi bisa dijalankan langsung di
 * terminal (TTY asli) tanpa melewati JavaFX Application launcher — JLine butuh
 * TTY asli agar warna dan ukuran layar terbaca benar.
 */
public class CliLauncher {
    public static void main(String[] args) {
        CliManager.start();
    }
}
